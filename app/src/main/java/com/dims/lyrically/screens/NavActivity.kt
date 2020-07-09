package com.dims.lyrically.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dims.lyrically.R

class NavActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Transition back to regular theme
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
    }
}