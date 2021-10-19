package com.sergeyrodin.matchesboxes

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity

fun launchAndMonitorMainActivity(idlingResource: DataBindingIdlingResource): ActivityScenario<MainActivity> {
    val activityScenario = ActivityScenario.launch(MainActivity::class.java)
    idlingResource.monitorActivity(activityScenario)
    return activityScenario
}

fun moveToPopular() {
    Espresso.onView(ViewMatchers.withId(R.id.popularComponentsFragment))
        .perform(ViewActions.click())
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
    Espresso.onView(ViewMatchers.isAssignableFrom(AutoCompleteTextView::class.java))
        .perform(ViewActions.typeText(query))
        .perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
}

fun moveToSearch() {
    onView(withId(R.id.searchFragment)).perform(click())
}