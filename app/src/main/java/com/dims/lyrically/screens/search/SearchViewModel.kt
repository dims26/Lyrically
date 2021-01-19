package com.dims.lyrically.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dims.lyrically.models.SearchCache
import com.dims.lyrically.utils.LoadState.*
import com.dims.lyrically.models.Song
import com.dims.lyrically.utils.LyricDataProvider
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.LoadState
import kotlinx.coroutines.*

class SearchViewModel(private val repo: Repository) : ViewModel() {

    private lateinit var textChangeJob : Job

    private val _songs = mutableListOf<Song>()
    val songs: List<Song> get() = _songs

    private val _songCaches = mutableListOf<Song>()
    val songCaches: List<Song> get() = _songCaches

    private val _loadingIndicator = MutableLiveData(IDLE)
    val loadingIndicator: LiveData<LoadState> get() = _loadingIndicator

    private val _cacheLoadingIndicator = MutableLiveData(IDLE)
    val cacheLoadingIndicator: LiveData<LoadState> get() = _cacheLoadingIndicator

    init {
        textChangeJob = Job()
    }

    fun search(query: String, provider: LyricDataProvider) {
        _loadingIndicator.value = LOADING
        repo.search(query, _loadingIndicator, _songs, provider)
    }

    fun searchCache(query: String) {
        _cacheLoadingIndicator.value = LOADING

        if (textChangeJob.isActive)
            textChangeJob.cancel()

        textChangeJob = viewModelScope.launch {
            delay(1000)
            val songs = repo.getCaches(query)
                    .map { Song(it.fullTitle, it.title, it.songArtImageThumbnailUrl, it.url, it.titleWithFeatured, it.id, it.artistName) }
            _songCaches.clear()
            _songCaches.addAll(songs)
            _cacheLoadingIndicator.postValue(LOADED)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textChangeJob.cancel()
    }
}
