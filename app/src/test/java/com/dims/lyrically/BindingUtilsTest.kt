package com.dims.lyrically

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dims.lyrically.utils.artistName
import com.dims.lyrically.utils.picasso
import com.dims.lyrically.utils.thumbnail
import com.dims.lyrically.utils.titleWithFeatured
import com.nhaarman.mockitokotlin2.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private var picassoFlag = 0

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])//todo Remove after upgrading AndroidStudio to v4, should fix Robolectric require Java 9 issue
class BindingUtilsTest {

    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()

    @Before
    fun setup(){
        if (picassoFlag == 0) {
            val context: Context = ApplicationProvider.getApplicationContext<Context>()
            Picasso.setSingletonInstance(Picasso.Builder(context).build())
            picassoFlag = 1
        }
    }

    private val textString = "TEST"
    private val emptyUrl = ""
    private val properLikeUrl = "https://images.rapgenius.com/6bxjwbfqghm9jqrtqt3d594y.300x300x1.jpg"
    private val defaultImageUrl = "http://assets.genius.com/images/default_cover_image.png?10000"
    private val mockImageView = mock<ImageView>()
    private val mockTextView = mock<TextView>()

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

//    @Test
//    fun test_thumbNailBinder_loadsGivenCorrectUrl(){
//        val givenUrl = properLikeUrl
//        val picassoMock = mock<Picasso>()
//
//        thumbnail(mockImageView, givenUrl, picassoMock)
//
//        argumentCaptor<RequestCreator>().apply {
//            verify(picassoMock, times(1)).load(givenUrl)
//        }
//    }
}