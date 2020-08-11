package com.dims.lyrically.screens.home

import android.os.Parcelable
import androidx.navigation.NavController
import com.dims.lyrically.ActivityProvider

interface HomeProvider : Parcelable, ActivityProvider {
    fun getHomeNavController(): NavController
}