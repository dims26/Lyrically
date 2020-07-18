package com.dims.lyrically.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtils(private val connectivityManager: ConnectivityManager) {

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT < 23) {
            val ni = connectivityManager.activeNetworkInfo
            if (ni != null) {
                (ni.isConnected && ((ni.type == ConnectivityManager.TYPE_WIFI) or (ni.type == ConnectivityManager.TYPE_MOBILE)))
            }else false
        } else {
            val n = connectivityManager.activeNetwork
            if (n != null) {
                val nc = connectivityManager.getNetworkCapabilities(n)
                (nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            }else false
        }
    }
}