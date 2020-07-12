package com.dims.lyrically.repository

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dims.lyrically.utils.LoadState
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.History
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.models.Song
import com.dims.lyrically.utils.LyricDataProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type


class Repository(private val db: LyricDatabase) {
    val favourites: LiveData<List<Favourites>> get() = _favourites
    private val _favourites =
            db.favouritesDao().favourites
    val history: LiveData<List<History>> get() = _history
    private val _history = db.historyDao().history

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

    fun search(query: String, indicator: MutableLiveData<LoadState>, songs: MutableList<Song>, provider: LyricDataProvider) {
        provider.search(query, getSearchCallback(indicator, songs))
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
                    indicator.postValue(LoadState.LOADED)
                } else {
                    indicator.postValue(LoadState.ERROR)
                    return
                }
            }
        }
    }
}

class SongListDeserializer :  JsonDeserializer<List<Song>?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Song>? {
        val jsonObject = json!!.asJsonObject
        val response = jsonObject["response"].asJsonObject
        val hits = response["hits"].asJsonArray

        val songs = mutableListOf<Song>()
        hits.forEach{
            val currentHit = it.asJsonObject
            if (currentHit["type"].asString != "song") return@forEach

            val result = currentHit["result"].asJsonObject
            val primaryArtist = result["primary_artist"].asJsonObject
            songs.add(Song(
                    result["full_title"].asString, result["title"].asString,
                    result["song_art_image_thumbnail_url"].asString, result["url"].asString,
                    result["title_with_featured"].asString, result["id"].asInt,
                    primaryArtist["name"].asString
            ))
        }
        return songs
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