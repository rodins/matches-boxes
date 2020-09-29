package com.sergeyrodin.matchesboxes.history

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.test.internal.util.Checks
import com.sergeyrodin.matchesboxes.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun hasBackgroundColor(colorRes: Int): Matcher<View> {
    Checks.checkNotNull(colorRes)
    return object: TypeSafeMatcher<View>() {

        override fun describeTo(description: Description?) {
            description?.appendText("background color: $colorRes")
        }

        override fun matchesSafely(item: View?): Boolean {
            if(item?.background == null) {
                return false
            }
            val actualColor = (item.background as ColorDrawable).color
            val expectedColor = ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color
            return actualColor == expectedColor
        }

    }
}

fun hasBackgroundColorAndText(colorRes: Int, text: String): Matcher<View> {
    return object: TypeSafeMatcher<View>() {

        override fun describeTo(description: Description?) {
            description?.appendText("text: $text, background color: $colorRes")
        }

        override fun matchesSafely(item: View?): Boolean {
            if(item?.background == null)
                return false
            val actualColor = (item.background as ColorDrawable).color
            val expectedColor = ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color

            val nameTextView = item.findViewById<TextView>(R.id.name_text)
            val actualText = nameTextView.text.toString()
            return actualColor == expectedColor && text == actualText
        }
    }
}