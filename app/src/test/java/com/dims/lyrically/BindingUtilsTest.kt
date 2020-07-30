package com.dims.lyrically

import android.widget.ImageView
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dims.lyrically.utils.artistName
import com.dims.lyrically.utils.thumbnail
import com.dims.lyrically.utils.titleWithFeatured
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.squareup.picasso.Picasso
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BindingUtilsTest {

    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()

    private val textString = "TEST"
    private val emptyUrl = ""
    private val properLikeUrl = "https://images.rapgenius.com/6bxjwbfqghm9jqrtqt3d594y.300x300x1.jpg"
    private val defaultImageUrl = "http://assets.genius.com/images/default_cover_image.png?10000"
    private val mockImageView = mock<ImageView>()
    private val mockTextView = mock<TextView>()

    @Before
    fun setup(){
        val context = InstrumentationRegistry.getInstrumentation().context
        Picasso.setSingletonInstance(Picasso.Builder(context).build())
    }

    @Test
    fun test_titleBinder_loadsText(){
        val expected = textString

        titleWithFeatured(mockTextView, expected)

        argumentCaptor<CharSequence>().apply {
            verify(mockTextView).text = capture()
            assertEquals(expected, firstValue)
        }
    }

    @Test
    fun test_artistNameBinder_loadsText(){
        val expected = textString

        artistName(mockTextView, expected)

        argumentCaptor<CharSequence>().apply {
            verify(mockTextView).text = capture()
            assertEquals(expected, firstValue)
        }
    }
}