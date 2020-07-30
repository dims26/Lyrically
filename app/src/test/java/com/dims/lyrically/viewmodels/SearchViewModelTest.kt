package com.dims.lyrically.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.search.SearchViewModel
import com.dims.lyrically.utils.LoadState
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val songList = listOf(
            Song("..", "..", "..", "..", "..", 1, ".."),
            Song("..", "..", "..", "..", "..", 2, "..")
    )

    @Test
    fun test_loadingIndicator_onClassInit() = runBlocking{
        val repository = mock<Repository>()
        val expected = LoadState.IDLE
        val viewModel = SearchViewModel(repository)

        val actual = viewModel.loadingIndicator.test().value()

        assertEquals(expected, actual)
    }

    @Test
    fun test_search_SuccessfulLoad() = runBlocking{
        val indicatorObserver = mock<Observer<LoadState>>()
        val repository = mock<Repository>{repository ->
            on(repository.search(any(), any(), any(), any()))
                    .doAnswer {
                        val songs = it.getArgument<MutableList<Song>>(2)
                        songs.clear()
                        songs.addAll(songList)
                        it.getArgument<MutableLiveData<LoadState>>(1).postValue(LoadState.LOADED)
            }
        }
        val viewModel = SearchViewModel(repository)
        viewModel.loadingIndicator.observeForever(indicatorObserver)
        val expected = songList

        viewModel.search("", mock())

        verify(indicatorObserver).onChanged(LoadState.LOADING)
        verify(indicatorObserver, never()).onChanged(LoadState.ERROR)
        verify(indicatorObserver).onChanged(LoadState.LOADED)
        assertEquals(viewModel.songs, expected)
    }

    @Test
    fun test_search_Error() = runBlocking{
        val indicatorObserver = mock<Observer<LoadState>>()
        val repository = mock<Repository>{repository ->
            on(repository.search(any(), any(), any(), any()))
                    .doAnswer {
                        it.getArgument<MutableLiveData<LoadState>>(1).postValue(LoadState.ERROR)
                    }
        }
        val viewModel = SearchViewModel(repository)
        viewModel.loadingIndicator.observeForever(indicatorObserver)
        val expected = listOf<Song>()

        viewModel.search("", mock())

        verify(indicatorObserver).onChanged(LoadState.LOADING)
        verify(indicatorObserver, never()).onChanged(LoadState.LOADED)
        verify(indicatorObserver).onChanged(LoadState.ERROR)
        assertEquals(viewModel.songs, expected)
    }
}