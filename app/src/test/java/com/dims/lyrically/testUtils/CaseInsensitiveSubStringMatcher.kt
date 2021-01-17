package com.dims.lyrically.testUtils

import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class CaseInsensitiveSubstringMatcher private constructor(private val subString: String) : TypeSafeMatcher<String>() {
    override fun matchesSafely(item: String): Boolean {
        return item.toLowerCase().contains(subString.toLowerCase())
    }

    override fun describeTo(description: org.hamcrest.Description) {
        description.appendText("containing substring $subString")
    }

    companion object {
        @Factory
        fun containsStringIgnoringCase(subString: String): Matcher<String> {
            return CaseInsensitiveSubstringMatcher(subString)
        }
    }

}