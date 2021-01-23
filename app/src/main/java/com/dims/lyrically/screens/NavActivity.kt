package com.dims.lyrically.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.dims.lyrically.ActivityProvider
import com.dims.lyrically.R
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.search.SearchFragment
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class NavActivity : AppCompatActivity(), ActivityProvider {

    @IgnoredOnParcel
    private lateinit var navHostFragment: NavHostFragment
    @IgnoredOnParcel
    private lateinit var navController: NavController
    override fun getActivityNavContainer(): Fragment? {
        return if (::navHostFragment.isInitialized)
            navHostFragment
        else
            null
    }

    override fun getRepo(): Repository = Repository(LyricDatabase.getDbInstance(this))
    override fun setToolbarAsActionbar(toolbar: Toolbar) { setSupportActionBar(toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Transition back to regular theme
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_container)!! as NavHostFragment
        navController = navHostFragment.navController


        //Manually create nav_graph_home and add activityProvider as argument
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)
        val providerArg = NavArgument.Builder()
                .setDefaultValue(this as ActivityProvider).build()
        graph.addArgument("provider", providerArg)
        navController.graph = graph


        navHostFragment.navController.addOnDestinationChangedListener{ _, destination, _ ->
            if((destination as FragmentNavigator.Destination).className != SearchFragment::class.java.name){
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
            }
        }
    }
}