package com.sergeyrodin.matchesboxes

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {
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
        dataSource = ServiceLocator.provideRadioComponentsDataSource(ApplicationProvider.getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetDataSource()
    }

    // Bag

    @Test
    fun addBag_navigateBack() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))
        pressBack()
        onView(withId(R.id.no_bags_added_text)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBag_navigateUp() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.no_bags_added_text)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun savedBag_navigationBack() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }

        activityScenario.close()
    }

    @Test
    fun mainScreen_navigateBack() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }

        activityScenario.close()
    }

    // Set

    @Test
    fun addSet_navigateBack() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())

        pressBack()

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_navigateUp() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun savedSet_navigateBack() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("New set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun savedSet_navigateUp() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("New set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    // Box

    // Component

}
