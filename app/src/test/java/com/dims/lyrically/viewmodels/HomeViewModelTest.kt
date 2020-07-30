package com.dims.lyrically.viewmodels

import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.home.HomeViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val mainThreadSurrogate = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun test_clearHistory() = runBlocking{
        val repo = mock<Repository>()
        val viewModel = HomeViewModel(repo)

        viewModel.clearHistory()

        verify(repo).clearHistory()
    }

}