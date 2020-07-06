package com.dims.lyrically

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.LoadState.*
import com.dims.lyrically.models.Song

class SearchViewModel(private val repo: Repository) : ViewModel() {
    private val _songs = MutableList<Song>()
    val songs: List<Song> get() = _songs
    private val _loadingIndicator = MutableLiveData(IDLE)
    val loadingIndicator: LiveData<LoadState> get() = _loadingIndicator
    fun search(query: String, context: Context) {
        repo.search(query, _loadingIndicator, _songs, context)
        _loadingIndicator.postValue(LOADING)
    }

}
