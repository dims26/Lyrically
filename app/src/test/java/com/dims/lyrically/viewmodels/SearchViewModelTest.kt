package com.dims.lyrically.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dims.lyrically.models.SearchCache
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.search.SearchViewModel
import com.dims.lyrically.utils.LoadState
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = TestCoroutineDispatcher()
    private lateinit var repository : Repository
    private lateinit var viewModel: SearchViewModel

    private val songList = listOf(
            Song("..", "..", "..", "..", "..", 1, ".."),
            Song("..", "..", "..", "..", "..", 2, "..")
    )

    private val searchCaches = listOf(
            SearchCache(1, "..", "..", "..", "..", "..", ".."),
            SearchCache(1, "..", "..", "..", "..", "..", "..")
    )

    @Before
    fun setUp() {
        repository = mock()
        viewModel = SearchViewModel(repository)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.cleanupTestCoroutines()
    }

    @Test
    fun test_loadingIndicators_IdleOnClassInit() = runBlocking{
        val expected = LoadState.IDLE

        val actual1 = viewModel.loadingIndicator.test().value()
        val actual2 = viewModel.loadingIndicator.test().value()

        assertThat(expected, allOf(equalTo(actual1), equalTo(actual2)))
    }

    @Test
    fun test_search_SuccessfulLoad() = runBlocking{
        val indicatorObserver = mock<Observer<LoadState>>()
        whenever(repository.search(any(), any(), any(), any()))
                .thenAnswer {
                    val songs = it.getArgument<MutableList<Song>>(2)
                    songs.clear()
                    songs.addAll(songList)
                    it.getArgument<MutableLiveData<LoadState>>(1).postValue(LoadState.LOADED)
                }
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
        whenever(repository.search(any(), any(), any(), any())).thenAnswer {
            it.getArgument<MutableLiveData<LoadState>>(1).postValue(LoadState.ERROR)
        }
        viewModel.loadingIndicator.observeForever(indicatorObserver)
        val expected = listOf<Song>()

        viewModel.search("", mock())

        verify(indicatorObserver).onChanged(LoadState.LOADING)
        verify(indicatorObserver, never()).onChanged(LoadState.LOADED)
        verify(indicatorObserver).onChanged(LoadState.ERROR)
        assertEquals(viewModel.songs, expected)
    }

    @Test
    //todo fix failing test
    fun test_searchCache() = runBlockingTest {
        val cacheLoadIndicatorObserver = mock<Observer<LoadState>>()
        val query = "Spa"
        val caches = searchCaches
        val expected = caches.map {
            Song(it.fullTitle, it.title, it.songArtImageThumbnailUrl, it.url, it.titleWithFeatured, it.id, it.artistName)
        }
        whenever(repository.getCaches(eq(query))).thenReturn(caches)
        viewModel.cacheLoadingIndicator.observeForever(cacheLoadIndicatorObserver)

        viewModel.searchCache(query)

        mainThreadSurrogate.advanceTimeBy(1000)
        verify(repository).getCaches(eq(query))
        verify(cacheLoadIndicatorObserver, times(1)).onChanged(LoadState.LOADING)
        verify(cacheLoadIndicatorObserver, times(1)).onChanged(LoadState.LOADED)
        assertEquals(viewModel.songCaches, expected)
    }
}