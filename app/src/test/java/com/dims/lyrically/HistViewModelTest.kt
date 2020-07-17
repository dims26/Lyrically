package com.dims.lyrically

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dims.lyrically.models.History
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.history.HistViewModel
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*

@ExperimentalCoroutinesApi
class HistViewModelTest {

    @get:Rule
    val instantExecutionRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = TestCoroutineDispatcher()
    private lateinit var repository : Repository

    private val hists = listOf(
            History(1, "..", "..", "..", "..", "..", ".."),
            History(2, "..", "..", "..", "..", "..", "..")
    )
    private val _histLiveData = MutableLiveData<List<History>>()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        repository = mock{repository ->
            on(repository.history).doAnswer{
                _histLiveData
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun test_historyLiveData_onChanged() = runBlocking{
        val observer = mock<Observer<List<History>>>()
        val viewModel = HistViewModel(repository)
        viewModel.history.observeForever(observer)
        val expected = hists

        _histLiveData.value = expected

        verify(observer).onChanged(com.nhaarman.mockitokotlin2.check {
            Assert.assertEquals(it[0].id, expected[0].id)
            Assert.assertEquals(it[1].id, expected[1].id)
            Assert.assertEquals(it.size, expected.size)
        })
    }

    @Test
    fun test_deleteHistory() = runBlocking{
        val viewModel = HistViewModel(repository)
        val expected = hists[0]

        viewModel.delete(expected)

        verify(repository).deleteFromHistory(expected)
    }
}