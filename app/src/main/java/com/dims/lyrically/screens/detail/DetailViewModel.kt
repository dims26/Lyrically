package com.dims.lyrically.screens.detail

import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.History
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.LyricWebChromeClient
import com.dims.lyrically.repository.LyricWebViewClient
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.NetworkUtils
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

    val favourites = repo.favourites

    fun updateHistory(song: Song) {
        uiScope.launch {
            val hist = with(song){
                History(id, fullTitle,
                        title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName)
            }
            if (repo.itemInstancesInHistoryCount(song.id) > 0){
                repo.updateHistory(hist)
            }else{
                repo.addHistory(hist)
            }
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
            } else {
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

    fun isNetworkAvailable(cm: ConnectivityManager?): Boolean =
        cm?.let { NetworkUtils(cm).isNetworkAvailable() } ?: false

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}