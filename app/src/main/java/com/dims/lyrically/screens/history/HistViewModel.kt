package com.dims.lyrically.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.models.History
import com.dims.lyrically.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HistViewModel(private val repo: Repository): ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun delete(history: History) {
        uiScope.launch {
            repo.deleteFromHistory(history)
        }
    }

    val history: LiveData<List<History>> get() = repo.history

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}