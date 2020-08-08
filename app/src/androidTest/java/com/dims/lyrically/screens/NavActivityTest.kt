package com.dims.lyrically.screens

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dims.lyrically.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(NavActivity::class.java)

    @Test
    fun navActivityTest() {
        val bottomNavigationItemView = onView(
                allOf(withId(R.id.histFragment), withContentDescription("History"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()))
        bottomNavigationItemView.perform(click())

        val bottomNavigationItemView2 = onView(
                allOf(withId(R.id.favFragment), withContentDescription("Favourites"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                0),
                        isDisplayed()))
        bottomNavigationItemView2.perform(click())

        val viewGroup = onView(
                allOf(withId(R.id.toolbar), childAtPosition(childAtPosition(withId(R.id.nav_container), 0), 0), isDisplayed()))
        viewGroup.check(matches(isDisplayed()))

        val imageButton = onView(
                allOf(withId(R.id.searchButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_container),
                                        0),
                                2),
                        isDisplayed()))
        imageButton.check(matches(isDisplayed()))

        val imageView = onView(
                allOf(withContentDescription("Error Indicator"),
                        childAtPosition(
                                allOf(withId(R.id.error_indicator),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                                0)),
                                0),
                        isDisplayed()))
        imageView.check(matches(isDisplayed()))

        val textView = onView(
                allOf(withText("Nothing here to show"),
                        childAtPosition(
                                allOf(withId(R.id.error_indicator),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                                0)),
                                1),
                        isDisplayed()))
        textView.check(matches(withText("Nothing here to show")))

        val textView2 = onView(
                allOf(withText("Get to searching"),
                        childAtPosition(
                                allOf(withId(R.id.error_indicator),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                                0)),
                                2),
                        isDisplayed()))
        textView2.check(matches(withText("Get to searching")))

        val imageView2 = onView(
                allOf(withId(R.id.icon),
                        childAtPosition(
                                allOf(withId(R.id.favFragment), withContentDescription("Favourites"),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                                0)),
                                0),
                        isDisplayed()))
        imageView2.check(matches(isDisplayed()))

        val textView3 = onView(
                allOf(withId(R.id.largeLabel), withText("Favourites"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.favFragment), withContentDescription("Favourites")),
                                        1),
                                0),
                        isDisplayed()))
        textView3.check(matches(withText("Favourites")))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}