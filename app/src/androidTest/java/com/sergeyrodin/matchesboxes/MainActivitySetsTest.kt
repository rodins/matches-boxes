package com.sergeyrodin.matchesboxes

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.di.TestModule
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TestModule::class)
class MainActivitySetsTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataSource: RadioComponentsDataSource

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun initDataSource() {
        hiltRule.inject()
        runBlocking {
            dataSource.clearDatabase()
        }
    }

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

    @Test
    fun addMatchesBoxSet_nameMatches() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.no_matches_box_sets_added))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit)).perform(ViewActions.typeText("MBS1"))
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Bag"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("MBS1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addTwoSets_namesEqual() = runBlocking {
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.no_matches_box_sets_added))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit))
            .perform(ViewActions.typeText("Set"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Set"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit))
            .perform(ViewActions.typeText("Set2"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Set2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_deleteButtonNotDisplayed() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete))
            .check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @Test
    fun updateSet_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit))
            .perform(ViewActions.replaceText("Set updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Set updated"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.no_matches_boxes_added))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun updateSet_nameInListUpdated() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit))
            .perform(ViewActions.replaceText("Set updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Set updated"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun setClick_titleEquals() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_titleEquals() = runBlocking {
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.add_set))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editSet_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.update_set)).perform(ViewActions.click())

        activityScenario.close()
    }

    @Test
    fun editSet_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit))
            .check(ViewAssertions.matches(ViewMatchers.withText(set.name)))

        activityScenario.close()
    }

    @Test
    fun setDelete_bagTitleDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(bag.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }
}