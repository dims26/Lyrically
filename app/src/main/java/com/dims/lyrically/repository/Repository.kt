package com.dims.lyrically.repository

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.datasources.LyricsAPIDatasource
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.History
import com.dims.lyrically.models.SearchCache
import com.dims.lyrically.models.Song
import com.dims.lyrically.utils.LoadState
import com.dims.lyrically.utils.LyricDataProvider
import com.dims.lyrically.utils.SongListDeserializer
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.Date

//todo inject dispatchers and datasource, fix tests
class Repository(private val db:  LyricDatabase) : Parcelable {
    val favourites: LiveData<List<Favourites>> get() = _favourites
    private val _favourites =
            db.favouritesDao().favourites
    val history: LiveData<List<History>> get() = _history
    private val _history = db.historyDao().history

    constructor(parcel: Parcel) : this(
            db = parcel.readSerializable() as LyricDatabase
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            trimCaches()
        }
    }

    fun getLyricWebViewClient(isVisible: MutableLiveData<Boolean>, progress: MutableLiveData<Int>): LyricWebViewClient {
        return LyricWebViewClient(isVisible, progress)
    }

    fun getLyricWebChromeClient(progress: MutableLiveData<Int>): LyricWebChromeClient {
        return LyricWebChromeClient(progress)
    }

    suspend fun addFavourite(fav: Favourites) {
        withContext(Dispatchers.IO) {
            db.favouritesDao().addFavourite(fav)
        }
    }

    suspend fun deleteFromFavourite(favourite: Favourites) {
        withContext(Dispatchers.IO){
            db.favouritesDao().deleteFromFavourite(favourite)
        }
    }

    suspend fun addHistory(hist: History) {
        withContext(Dispatchers.IO){
            db.historyDao().addHistory(hist)
        }
    }

    suspend fun updateHistory(hist: History) {
        withContext(Dispatchers.IO){
            db.historyDao().updateHistory(hist)
        }
    }

    suspend fun deleteFromHistory(history: History) {
        withContext(Dispatchers.IO){
            db.historyDao().deleteFromHistory(history)
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO){
            db.historyDao().clearHistory()
        }
    }

    suspend fun itemInstancesInFavouritesCount(id: Int) : Int{
        return withContext(Dispatchers.IO){
            db.favouritesDao().usersCount(id)
        }
    }

    suspend fun itemInstancesInHistoryCount(id: Int) : Int{
        return withContext(Dispatchers.IO){
            db.historyDao().usersCount(id)
        }
    }

    private suspend fun trimCaches(){
        withContext(Dispatchers.IO){
            db.searchCacheDao().clearCache(Date())
        }
    }

    suspend fun addCaches(caches: Array<SearchCache>){
        withContext(Dispatchers.IO){
            db.searchCacheDao().addCache(*caches)
        }
    }

    suspend fun getCaches(query: String): List<SearchCache> {
        lateinit var result : List<SearchCache>
        withContext(Dispatchers.IO) {
            result = db.searchCacheDao().getSearchCache(query)
        }
        return result
    }

    //todo fix tests and remove, implementation switched to suspend function
    fun search(query: String, indicator: MutableLiveData<LoadState>, songs: MutableList<Song>, provider: LyricDataProvider) {
        provider.search(query, getSearchCallback(indicator, songs))
    }

    suspend fun search(query: String, datasource: LyricsAPIDatasource) = //todo use injected datasource!
        withContext(Dispatchers.IO) {//todo use injected dispatcher
            val searchSongs = datasource.search(query)
            saveCache(searchSongs)//Save loaded songs to search cache
            return@withContext searchSongs
        }

    /** Map [songs] to a list of [SearchCache] and save to database
     */
    private fun saveCache(songs: List<Song>){
        val searchCaches = songs.map{ song -> with(song){
            SearchCache(id, fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName)
        } }.toTypedArray()
        CoroutineScope(Dispatchers.IO).launch {
            addCaches(searchCaches)
        }
    }

    fun getSearchCallback(indicator: MutableLiveData<LoadState>, songs: MutableList<Song>) : Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                indicator.postValue(LoadState.ERROR)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    val gsonBuilder = GsonBuilder()
                    val deserializer = SongListDeserializer()
                    gsonBuilder.registerTypeAdapter(Song::class.java, deserializer)

                    val gson = gsonBuilder.create()

                    songs.clear()
                    songs.addAll(gson.fromJson(response.body?.string(), object : TypeToken<Song>() {}.type))
                    saveCache(songs)//Save loaded song to search cache
                    indicator.postValue(LoadState.LOADED)
                } else {
                    indicator.postValue(LoadState.ERROR)
                    return
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(db)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Repository> {
        override fun createFromParcel(parcel: Parcel): Repository {
            return Repository(parcel)
        }

        override fun newArray(size: Int): Array<Repository?> {
            return arrayOfNulls(size)
        }
    }
}

class LyricWebViewClient(private val isVisible: MutableLiveData<Boolean>,
                         private val progress: MutableLiveData<Int>) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        progress.postValue(100)
        isVisible.postValue(false)
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        progress.postValue(0)
        isVisible.postValue(true)
    }
}

class LyricWebChromeClient(private val progress: MutableLiveData<Int>) : WebChromeClient() {
    override fun onProgressChanged(view: WebView, progress: Int) {
        this.progress.postValue(progress)
    }
}