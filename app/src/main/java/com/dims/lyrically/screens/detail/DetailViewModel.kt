package com.dims.lyrically.screens.detail

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.History
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.LyricWebChromeClient
import com.dims.lyrically.repository.LyricWebViewClient
import com.dims.lyrically.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: Repository): ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isVisible = MutableLiveData(true)
    val isVisible: LiveData<Boolean>
        get() = _isVisible

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int>
        get() = _progress

    val history = repo.history
    val favourites = repo.favourites
    private lateinit var song: Song

    private val histObserver= object : Observer<List<History>> {
        override fun onChanged(t: List<History>?) {
            if (t is List<History>)
            addToHistory(song)
            history.removeObserver(this)
        }
    }

    fun setupDbLiveData(song: Song){
        this.song = song
        history.observeForever(histObserver)
    }

    private fun addToHistory(song: Song) {
        uiScope.launch {
            val hist = with(song){
                History(id, fullTitle,
                        title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName)
            }
            if (history.value != null){
                if (repo.itemInstancesInHistoryCount(song.id) > 0){
                    repo.updateHistory(hist)
                }else{
                    repo.addHistory(hist)
                }}
        }
    }

    fun toggleFavourite(song: Song) {
        uiScope.launch {
            val fav = with(song){
                Favourites(id, fullTitle, title, songArtImageThumbnailUrl,
                        url, titleWithFeatured, artistName)
            }
            if (repo.itemInstancesInFavouritesCount(song.id) > 0){
                repo.deleteFromFavourite(fav)
            }
            else {
                repo.addFavourite(fav)
            }
        }
    }

    fun getLyricWebViewClient() : LyricWebViewClient {
        return repo.getLyricWebViewClient(_isVisible, _progress)
    }
    fun getLyricWebChromeClient() : LyricWebChromeClient {
        return repo.getLyricWebChromeClient(_progress)
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)

        return if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = connectivityManager.activeNetworkInfo
                if (ni != null) {
                    (ni.isConnected && ((ni.type == ConnectivityManager.TYPE_WIFI) or (ni.type == ConnectivityManager.TYPE_MOBILE)))
                }else false
            } else {
                val n = connectivityManager.activeNetwork
                if (n != null) {
                    val nc = connectivityManager.getNetworkCapabilities(n)
                    (nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                }else false
            }
        }else false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}