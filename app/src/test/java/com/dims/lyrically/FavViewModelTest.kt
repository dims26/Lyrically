package com.dims.lyrically

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.favourites.FavViewModel
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
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
class FavViewModelTest {

    @get:Rule
    val instantExecutionRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = TestCoroutineDispatcher()
    private lateinit var repository : Repository

    private val favs = listOf(
            Favourites(1, "..", "..", "..", "..", "..", ".."),
            Favourites(2, "..", "..", "..", "..", "..", "..")
    )
    private val _favLiveData = MutableLiveData<List<Favourites>>()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        repository = mock{repository ->
            on(repository.favourites).doAnswer{
                _favLiveData
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun test_favouritesLiveData_onChanged() = runBlocking{
        val observer = mock<Observer<List<Favourites>>>()
        val viewModel = FavViewModel(repository)
        viewModel.favourites.observeForever(observer)
        val expected = favs

        _favLiveData.value = expected

        verify(observer).onChanged(check {
            assertEquals(it[0].id, expected[0].id)
            assertEquals(it[1].id, expected[1].id)
            assertEquals(it.size, expected.size)
        })
    }

    @Test
    fun test_deleteHistory() = runBlocking{
        val viewModel = FavViewModel(repository)
        val expected = favs[0]

        viewModel.delete(expected)

        verify(repository).deleteFromFavourite(expected)
    }
}