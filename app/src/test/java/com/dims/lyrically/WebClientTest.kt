package com.dims.lyrically

import android.webkit.WebView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dims.lyrically.repository.LyricWebChromeClient
import com.dims.lyrically.repository.LyricWebViewClient
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebClientTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()
    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()

    private lateinit var isVisible: MutableLiveData<Boolean>
    private lateinit var progressLiveData: MutableLiveData<Int>
    private lateinit var webViewClient: LyricWebViewClient
    private lateinit var  webView: WebView

    private fun getSampleUrl() = "https://url.sample"

    @Before
    fun refreshValues(){
        progressLiveData = mock()
        isVisible = mock()
        webView  = mock()
        webViewClient = LyricWebViewClient(isVisible, progressLiveData)
    }

    @Test
    fun test_LyricWebChromeClient_onProgressChanged(){
        val webChromeClient = LyricWebChromeClient(progressLiveData)
        val progress = 30

        webChromeClient.onProgressChanged(webView, progress)

        verify(progressLiveData).postValue(progress)
    }

    @Suppress("DEPRECATION")
    @Test
    fun test_LyricWebViewClient_shouldOverrideUrlLoading(){
        val url = getSampleUrl()

        webViewClient.shouldOverrideUrlLoading(webView, url)

        verify(webView).loadUrl(url)
    }

    @Test
    fun test_LyricWebViewClient_onPageFinished(){
        val url = getSampleUrl()
        val expectedInt = 100
        val expectedBoolean = false

        webViewClient.onPageFinished(webView, url)

        verify(progressLiveData).postValue(expectedInt)
        verify(isVisible).postValue(expectedBoolean)
    }

    @Test
    fun test_LyricWebViewClient_onPageStarted(){
        val url = getSampleUrl()
        val expectedInt = 0
        val expectedBoolean = true

        webViewClient.onPageStarted(webView, url, null)

        verify(progressLiveData).postValue(expectedInt)
        verify(isVisible).postValue(expectedBoolean)
    }
}