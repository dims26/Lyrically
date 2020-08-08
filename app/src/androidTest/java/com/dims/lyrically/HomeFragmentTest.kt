package com.dims.lyrically

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dims.lyrically.screens.NavActivity
import com.dims.lyrically.screens.home.HomeFragment
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentTest {

    private lateinit var activityScenario: ActivityScenario<NavActivity>

    @Before
    fun setup(){
        activityScenario = launchActivity()
    }

    @After
    fun tearDown(){
        activityScenario.close()
    }

    @Test
    fun test_toolbar_shouldBeDisplayed() {
        activityScenario.onActivity {
            it.supportFragmentManager.fragments[0].childFragmentManager.fragments
        }
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }
//
//    @Test
//    fun test_fragmentHolder_shouldBeDisplayed() {
//        launchFragmentInContainer<HomeFragment>(themeResId = R.style.NoActionBar)
//        onView(withId(R.id.fragment)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_searchFAB_shouldBeDisplayed() {
//        launchFragmentInContainer<HomeFragment>(themeResId = R.style.NoActionBar)
//        onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_bottomNavigation_shouldBeDisplayed() {
//        launchFragmentInContainer<HomeFragment>(themeResId = R.style.NoActionBar)
//        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_bottomNavigation_favouritesItemSelectedByDefault() {
//        launchFragmentInContainer<HomeFragment>(themeResId = R.style.NoActionBar)
//        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_bottomNavigation_shouldNavigateToHistoryFragment() {
//        // Create a mock NavController
//        val mockNavController = mock<NavController>()
////        lateinit var historyMenuItem: MenuItem
//        // Create a graphical FragmentScenario for the LandingFragment
//        val scanScenario = launchFragmentInContainer<HomeFragment>(themeResId = R.style.NoActionBar)
//        // Set the NavController property on the fragment
//        scanScenario.onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), mockNavController)
//        }
//
//        // Verify that performing a click prompts the correct Navigation action
//        onView(withId(R.id.bottomNavigationView)).perform(NavigationViewActions.navigateTo(R.id.histFragment))
//
//        verify(mockNavController).navigate(R.id.histFragment, null, any())
//    }

}