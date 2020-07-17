package com.dims.lyrically.screens.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavViewModel(private val repo: Repository): ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val favourites: LiveData<List<Favourites>> = repo.favourites

    fun delete(favourite: Favourites) {
        uiScope.launch {
            repo.deleteFromFavourite(favourite)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}