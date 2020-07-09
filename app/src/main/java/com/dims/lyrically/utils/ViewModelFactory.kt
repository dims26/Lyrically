package com.dims.lyrically.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dims.lyrically.screens.home.HomeViewModel
import com.dims.lyrically.screens.search.SearchViewModel
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.detail.DetailViewModel
import com.dims.lyrically.screens.favourites.FavViewModel
import com.dims.lyrically.screens.history.HistViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FavViewModel::class.java) -> FavViewModel(repository) as T
            modelClass.isAssignableFrom(HistViewModel::class.java) -> HistViewModel(repository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(repository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(repository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            else -> throw IllegalArgumentException("expected FavViewModel, got null")
        }
    }
}
