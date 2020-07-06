package com.dims.lyrically

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.database.Favourites
import com.dims.lyrically.database.History
import com.dims.lyrically.models.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: Repository): ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _snackbar = MutableLiveData(-1)
    val snackbar: LiveData<Int> get() = _snackbar

    private val _isVisible = MutableLiveData(false)
    val isVisible: LiveData<Boolean>
        get() = _isVisible

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int>
            get() = _progress

    fun addToHistory(song: Song) {
        uiScope.launch {
            val hist = with(song){History(id, fullTitle,
                    title, songArtImageThumbnailUrl, url, titleWithFeatured, artistName)}
            if (!repo.history.value!!.contains(hist)){
                repo.addHistory(hist)

            }else{
                repo.updateHistory(hist)
            }
        }
    }

    fun toggleFavourite(song: Song) {
        uiScope.launch {
            val fav = with(song){
                Favourites(id, fullTitle, title, songArtImageThumbnailUrl,
                        url, titleWithFeatured, artistName)
            }
            if (repo.favourites.value!!.contains(fav)){
                repo.deleteFavourite(fav)
                _snackbar.postValue(2)
            }
            else {
                repo.addFavourite(fav)
                _snackbar.postValue(1)
            }
        }
    }

    //TODO return both web clients to view model
    fun getLyricWebViewClient() : LyricWebViewClient{
        return repo.getLyricWebViewClient(_isVisible, _progress)
    }
    fun getLyricWebChromeClient() : LyricWebChromeClient{
        return repo.getLyricWebChromeClient(_progress)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}