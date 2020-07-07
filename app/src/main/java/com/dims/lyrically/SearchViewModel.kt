package com.dims.lyrically

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.LoadState.*
import com.dims.lyrically.models.Song
import com.dims.lyrically.providers.LyricDataProvider

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
