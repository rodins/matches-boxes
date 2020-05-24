package com.sergeyrodin.matchesboxes

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var dataSource: RadioComponentsDataSource

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Before
    fun init() {
        dataSource = ServiceLocator.provideRadioComponentsDataSource(getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun addBag_bagNameDisplayed() = runBlocking{
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())
        onView(withText("New bag")).check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun addBag_deleteButtonIsNotDisplayed() = runBlocking{
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())

        onView(withId(R.id.action_delete)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun noBags_addBagClick_textEquals() = runBlocking{
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.no_bags_added_text)).check(matches(isDisplayed()))
        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))

        activityScenario.close()
    }

    @Test
    fun addBag_showToast() = runBlocking{
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var activity: MainActivity? = null
        activityScenario.onActivity{
            activity = it
        }

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        onView(withText(R.string.bag_added))
            .inRoot(withDecorView(not(activity?.window?.decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

}