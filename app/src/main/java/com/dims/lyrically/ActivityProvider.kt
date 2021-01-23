package com.dims.lyrically


import android.os.Parcelable
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.dims.lyrically.repository.Repository
import kotlinx.android.parcel.Parcelize

interface ActivityProvider : Parcelable {
    fun getActivityNavContainer(): Fragment?
    fun getRepo(): Repository
    fun setToolbarAsActionbar(toolbar: Toolbar)
}