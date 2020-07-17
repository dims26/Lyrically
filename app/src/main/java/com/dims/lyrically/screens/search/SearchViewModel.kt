package com.dims.lyrically.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.utils.LoadState.*
import com.dims.lyrically.models.Song
import com.dims.lyrically.utils.LyricDataProvider
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.LoadState

class SearchViewModel(private val repo: Repository) : ViewModel() {
    private val _songs = mutableListOf<Song>()
    val songs: List<Song> get() = _songs

    private val _loadingIndicator = MutableLiveData(IDLE)
    val loadingIndicator: LiveData<LoadState> get() = _loadingIndicator

    fun search(query: String, provider: LyricDataProvider) {
        _loadingIndicator.value = LOADING
        repo.search(query, _loadingIndicator, _songs, provider)
    }
}
