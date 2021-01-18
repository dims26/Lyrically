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

    private val _songs = mutableListOf<Song>()
    val songs: List<Song> get() = _songs

    private val _searchCaches = mutableListOf<SearchCache>()
    val searchCaches: List<SearchCache> get() = _searchCaches

    private val _loadingIndicator = MutableLiveData(IDLE)
    val loadingIndicator: LiveData<LoadState> get() = _loadingIndicator

    private val _cacheLoadingIndicator = MutableLiveData(IDLE)
    val cacheLoadingIndicator: LiveData<LoadState> get() = _cacheLoadingIndicator

    fun search(query: String, provider: LyricDataProvider) {
        _loadingIndicator.value = LOADING
        repo.search(query, _loadingIndicator, _songs, provider)
    }

    fun searchCache(query: String){
        _cacheLoadingIndicator.value = LOADING

        if (viewModelScope.coroutineContext.isActive)
            viewModelScope.coroutineContext.cancel()

        viewModelScope.launch {
            delay(1000)
            _searchCaches.clear()
            _searchCaches.addAll(repo.getCaches(query))
            _cacheLoadingIndicator.postValue(LOADED)
        }
    }
}
