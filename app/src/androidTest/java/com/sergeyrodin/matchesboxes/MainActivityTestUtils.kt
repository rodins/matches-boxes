package com.sergeyrodin.matchesboxes

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity

fun launchAndMonitorMainActivity(idlingResource: DataBindingIdlingResource): ActivityScenario<MainActivity> {
    val activityScenario = ActivityScenario.launch(MainActivity::class.java)
    idlingResource.monitorActivity(activityScenario)
    return activityScenario
}

fun moveToPopular() {
    onView(withId(R.id.popularComponentsFragment)).perform(click())
}

fun rotateDeviceToLandscape(
    activity: Activity?,
    activityScenario: ActivityScenario<MainActivity>,
    dataBindingIdlingResource: DataBindingIdlingResource
) {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    dataBindingIdlingResource.monitorActivity(activityScenario)
}


fun rotateDeviceToPortrait(
    activity: Activity?,
    activityScenario: ActivityScenario<MainActivity>,
    dataBindingIdlingResource: DataBindingIdlingResource
) {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    dataBindingIdlingResource.monitorActivity(activityScenario)
}

fun typeQuery(query: String) {
    onView(ViewMatchers.isAssignableFrom(AutoCompleteTextView::class.java))
        .perform(ViewActions.typeText(query))
        .perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
}

fun moveToSearch() {
    onView(withId(R.id.searchFragment)).perform(click())
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.onNodeWithContentDescription(
    resource: Int
) = onNodeWithContentDescription(activity.getString(resource))

@JvmName("onNodeWithContentDescriptionHiltTestActivityHiltTestActivity")
fun AndroidComposeTestRule<ActivityScenarioRule<HiltTestActivity>, HiltTestActivity>.onNodeWithContentDescription(
    resource: Int
) = onNodeWithContentDescription(activity.getString(resource))

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.onNodeWithText(
    resource: Int
) = onNodeWithText(activity.getString(resource))

@JvmName("onNodeWithTextHiltTestActivityHiltTestActivity")
fun AndroidComposeTestRule<ActivityScenarioRule<HiltTestActivity>, HiltTestActivity>.onNodeWithText(
    resource: Int
) = onNodeWithText(activity.getString(resource))
