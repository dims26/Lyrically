package com.dims.lyrically.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dims.lyrically.datasources.LyricsAPIDatasource
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.LoadState
import com.dims.lyrically.utils.LoadState.ERROR
import com.dims.lyrically.utils.LoadState.IDLE
import com.dims.lyrically.utils.LoadState.LOADED
import com.dims.lyrically.utils.LoadState.LOADING
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class SearchViewModel(private val repo: Repository) : ViewModel() {

    private var searchCacheJob : Job
    private var searchJob : Job

    private val _songs = mutableListOf<Song>()
    val songs: List<Song> get() = _songs

    private val _songCaches = mutableListOf<Song>()
    val songCaches: List<Song> get() = _songCaches

    private val _loadingIndicator = MutableLiveData(IDLE)
    val loadingIndicator: LiveData<LoadState> get() = _loadingIndicator

    private val _cacheLoadingIndicator = MutableLiveData(IDLE)
    val cacheLoadingIndicator: LiveData<LoadState> get() = _cacheLoadingIndicator

    init {
        searchCacheJob = Job()
        searchJob = Job()
    }

    fun search(query: String, datasource: LyricsAPIDatasource) {
        _loadingIndicator.value = LOADING

        if (searchJob.isActive) searchJob.cancel()

        searchJob = viewModelScope.launch {
            yield()
            try {
                val songs = repo.search(query, datasource)
                _songs.clear()
                _songs.addAll(songs)
                _loadingIndicator.postValue(LOADED)
            } catch (e: Exception) {
                _loadingIndicator.postValue(ERROR)
            }
        }
    }

    fun searchCache(query: String) {
        _cacheLoadingIndicator.value = LOADING

        if (searchCacheJob.isActive)
            searchCacheJob.cancel()

        searchCacheJob = viewModelScope.launch {
            delay(1000)
            val caches = repo.getCaches(query)
                    .map { Song(it.fullTitle, it.title, it.songArtImageThumbnailUrl, it.url, it.titleWithFeatured, it.id, it.artistName) }
            _songCaches.clear()
            _songCaches.addAll(caches)
            _cacheLoadingIndicator.postValue(LOADED)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchCacheJob.cancel()
    }
}
