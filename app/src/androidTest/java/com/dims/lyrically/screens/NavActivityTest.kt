package com.dims.lyrically.screens

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dims.lyrically.R
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.screens.favourites.FavouritesRecyclerAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavActivityTest {

    private val favourites = listOf<Favourites>(
            Favourites(2357082, "Army of One by Coldplay","Army of One" , "https://images.genius.com/c641370206d55a98e51f3e5668d556de.300x300x1.jpg", "https://genius.com/Coldplay-army-of-one-lyrics","Army of One" , "Coldplay"),
            Favourites(3117181, "Believer (Kaskade Remix) by Imagine Dragons","Believer (Kaskade Remix)" , "https://images.genius.com/34373b07f8d6b2f277abde6221c9e625.300x300x1.jpg", "https://genius.com/Imagine-dragons-believer-kaskade-remix-lyrics","Believer (Kaskade Remix)" , "Imagine Dragons"),
            Favourites(349262, "Delicate by Damien Rice","Delicate" , "https://images.genius.com/d3a23cb51977431abb3b0f24fd0c8549.300x300x1.jpg", "https://genius.com/Damien-rice-delicate-lyrics","Delicate" , "Damien Rice"),
            Favourites(109206, "Demons by Imagine Dragons","Demons" , "https://images.genius.com/cc614ee6032a8b68a85647e0b3b32899.300x300x1.jpg", "https://genius.com/Imagine-dragons-demons-lyrics","Demons" , "Imagine Dragons"),
            Favourites(2357082, "Army of One by Coldplay","Army of One" , "https://images.genius.com/c641370206d55a98e51f3e5668d556de.300x300x1.jpg", "https://genius.com/Coldplay-army-of-one-lyrics","Army of One" , "Coldplay"),
            Favourites(3117181, "Believer (Kaskade Remix) by Imagine Dragons","Believer (Kaskade Remix)" , "https://images.genius.com/34373b07f8d6b2f277abde6221c9e625.300x300x1.jpg", "https://genius.com/Imagine-dragons-believer-kaskade-remix-lyrics","Believer (Kaskade Remix)" , "Imagine Dragons"),
            Favourites(349262, "Delicate by Damien Rice","Delicate" , "https://images.genius.com/d3a23cb51977431abb3b0f24fd0c8549.300x300x1.jpg", "https://genius.com/Damien-rice-delicate-lyrics","Delicate" , "Damien Rice"),
            Favourites(109206, "Demons by Imagine Dragons","Demons" , "https://images.genius.com/cc614ee6032a8b68a85647e0b3b32899.300x300x1.jpg", "https://genius.com/Imagine-dragons-demons-lyrics","Demons" , "Imagine Dragons")
    )

    @Rule
    @JvmField
    var activityScenarioRule = ActivityScenarioRule(NavActivity::class.java)

    @Suppress("UNCHECKED_CAST")
    @Before
    fun populateRecycler(){
        activityScenarioRule.scenario.onActivity {
            val adapter = it.supportFragmentManager.findFragmentById(R.id.nav_container)!!
                    .childFragmentManager.findFragmentById(R.id.homeFragment)!!
                    .childFragmentManager.findFragmentById(R.id.fragment)!!
                    .childFragmentManager.findFragmentById(R.id.favFragment)!!
                    .requireView().findViewById<RecyclerView>(R.id.fav_recycler)
                    .adapter as ListAdapter<Favourites, FavouritesRecyclerAdapter.ViewHolder>
            adapter.submitList(favourites)
        }
    }


}