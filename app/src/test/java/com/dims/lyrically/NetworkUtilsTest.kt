package com.dims.lyrically

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.dims.lyrically.utils.NetworkUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetworkCapabilities
import org.robolectric.shadows.ShadowNetworkInfo
import org.robolectric.util.ReflectionHelpers
import java.io.IOException


@RunWith(RobolectricTestRunner::class)
class NetworkUtilsTest {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var shadowConnectivityManager: ShadowConnectivityManager
    private lateinit var shadowOfActiveNetworkInfo: ShadowNetworkInfo
    private lateinit var networkUtils: NetworkUtils

    @Suppress("DEPRECATION")
    @Before
    @Throws(IOException::class)
    fun setUp() {
        connectivityManager = getConnectivityManager()!!
        shadowConnectivityManager = shadowOf(connectivityManager)
        shadowOfActiveNetworkInfo = shadowOf(connectivityManager.activeNetworkInfo)
        networkUtils = NetworkUtils(connectivityManager)
    }

    @Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP_MR1])
    fun test_connectivityManagerModifiable() {
        val networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_WIFI, 0, true, true)
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val activeInfo = connectivityManager.activeNetworkInfo

        assertTrue(activeInfo != null && activeInfo.isConnected)
        assertEquals(ConnectivityManager.TYPE_WIFI, activeInfo.type)
    }

    @Suppress("DEPRECATION")
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP_MR1])
    fun test_isNetworkAvailable_pre23Build_mobileNetworkConnected() {
        val networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_MOBILE, 0, true, true)
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val actual = networkUtils.isNetworkAvailable()

        assertTrue(actual)
    }

    @Suppress("DEPRECATION")
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP_MR1])
    fun test_isNetworkAvailable_pre23Build_wifiNetworkConnected() {
        val networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_WIFI, 0, true, true)
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val actual = networkUtils.isNetworkAvailable()

        assertTrue(actual)
    }

    @Suppress("DEPRECATION")
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP_MR1])
    fun test_isNetworkAvailable_pre23Build_otherNetworkConnected() {
        val networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_WIMAX, 0, true, true)
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val actual = networkUtils.isNetworkAvailable()

        assertFalse(actual)
    }

    @Suppress("DEPRECATION")
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP_MR1])
    fun test_isNetworkAvailable_pre23Build_disconnected() {
        val networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.DISCONNECTED,
                ConnectivityManager.TYPE_MOBILE, 0, true, NetworkInfo.State.DISCONNECTED)
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val actual = networkUtils.isNetworkAvailable()

        assertFalse(actual)
    }

    private fun getConnectivityManager(): ConnectivityManager? {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return ContextCompat.getSystemService(context, ConnectivityManager::class.java)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun test_isNetworkAvailable_post23Build_mobileNetworkConnected() {
        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)


        val actual = networkUtils.isNetworkAvailable()

        assertTrue(actual)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun test_isNetworkAvailable_post23Build_wifiNetworkConnected() {
        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)


        val actual = networkUtils.isNetworkAvailable()

        assertTrue(actual)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun test_isNetworkAvailable_post23Build_otherNetworkConnected() {
        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)


        val actual = networkUtils.isNetworkAvailable()

        assertFalse(actual)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun test_isNetworkAvailable_post23Build_disconnected() {
        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)


        val actual = networkUtils.isNetworkAvailable()

        assertFalse(actual)
    }
}