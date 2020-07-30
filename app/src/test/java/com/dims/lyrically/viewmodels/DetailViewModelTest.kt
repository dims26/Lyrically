package com.dims.lyrically.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dims.lyrically.models.History
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.detail.DetailViewModel
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val instantTaskEXecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = TestCoroutineDispatcher()
    private lateinit var repository : Repository
    private lateinit var viewModel: DetailViewModel

    private val songList = listOf(
            Song("..", "..", "..", "..", "..", 1, ".."),
            Song("..", "..", "..", "..", "..", 2, "..")
    )

    private val hists = listOf(
            History(2, "..", "..", "..", "..", "..", ".."),
            History(3, "..", "..", "..", "..", "..", ".."),
            History(4, "..", "..", "..", "..", "..", "..")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        repository = mock()
        viewModel = DetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun test_loadingIndicators_onClassInit() = runBlocking{
        val expectedVisibility = true
        val expectedProgress = 0

        val actualVisibility = viewModel.isVisible.test().value()
        val actualProgress = viewModel.progress.test().value()

        assertEquals(expectedVisibility, actualVisibility)
        assertEquals(expectedProgress, actualProgress)
    }

    @Test
    fun test_updateHistory_newSong() = runBlocking {
        val expectedSong = songList[0]
        whenever(repository.itemInstancesInHistoryCount(expectedSong.id)).thenReturn(0)

        viewModel.updateHistory(expectedSong)

        verify(repository).addHistory(any())
        verify(repository, never()).updateHistory(any())
    }

    @Test
    fun test_updateHistory_existingSong() = runBlocking {
        val expectedSong = songList[1]
        whenever(repository.itemInstancesInHistoryCount(expectedSong.id)).thenReturn(1)

        viewModel.updateHistory(expectedSong)

        verify(repository).updateHistory(argThat { id == expectedSong.id })
        verify(repository, never()).addHistory(any())
    }

    @Test
    fun test_toggleFavourite_isNotFavourite() = runBlocking {
        val expectedSong = songList[0]
        whenever(repository.itemInstancesInFavouritesCount(expectedSong.id)).thenReturn(0)

        viewModel.toggleFavourite(expectedSong)

        verify(repository).addFavourite(argThat { id == expectedSong.id })
        verify(repository, never()).deleteFromFavourite(any())
    }

    @Test
    fun test_toggleFavourite_isFavourite() = runBlocking {
        val expectedSong = songList[1]
        whenever(repository.itemInstancesInFavouritesCount(expectedSong.id)).thenReturn(1)

        viewModel.toggleFavourite(expectedSong)

        verify(repository).deleteFromFavourite(argThat { id == expectedSong.id })
        verify(repository, never()).addFavourite(any())
    }

    @Test
    fun test_getLyricWebViewClient(){
        val isVisibility = viewModel.isVisible.test().value()
        val progress = viewModel.progress.test().value()

        viewModel.getLyricWebViewClient()

        verify(repository).getLyricWebViewClient(argThat { value == isVisibility }, argThat { value == progress })
    }

    @Test
    fun test_getLyricWebChromeClient(){
        val progress = viewModel.progress.test().value()

        viewModel.getLyricWebChromeClient()

        verify(repository).getLyricWebChromeClient(argThat { value == progress })
    }
}