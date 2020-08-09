package com.dims.lyrically.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.dims.lyrically.R
import com.dims.lyrically.screens.search.SearchFragment


class NavActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Transition back to regular theme
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_container)!! as NavHostFragment
        navHostFragment.navController.addOnDestinationChangedListener{ _, destination, _ ->
            if((destination as FragmentNavigator.Destination).className != SearchFragment::class.java.name){
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
            }
        }
    }
}