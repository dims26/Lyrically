package com.dims.lyrically

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dims.lyrically.database.Favourites
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavViewModel(private val repo: Repository): ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun delete(favourite: Favourites) {
        uiScope.launch {
            repo.deleteFromFavourite(favourite)
        }
    }

    val favourites: LiveData<List<Favourites>> = repo.favourites

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}