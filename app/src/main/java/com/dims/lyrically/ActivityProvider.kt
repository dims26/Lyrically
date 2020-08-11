package com.dims.lyrically


import android.os.Parcelable
import androidx.appcompat.widget.Toolbar
import com.dims.lyrically.repository.Repository
import kotlinx.android.parcel.Parcelize

interface ActivityProvider : Parcelable {
    fun getRepo(): Repository
    fun setToolbarAsActionbar(toolbar: Toolbar)
}