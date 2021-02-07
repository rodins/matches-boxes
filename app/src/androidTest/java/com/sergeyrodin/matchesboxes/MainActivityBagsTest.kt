package com.sergeyrodin.matchesboxes

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityBagsTest {

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

    @Test
    fun addBag_bagNameDisplayed() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.add_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .perform(ViewActions.typeText("New bag"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("New bag"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun addBag_deleteButtonIsNotDisplayed() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.add_bag_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.action_delete))
            .check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @Test
    fun noBags_addBagClick_textEquals() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.no_bags_added_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.add_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .check(ViewAssertions.matches(ViewMatchers.withHint(R.string.enter_bag_name)))

        activityScenario.close()
    }

    @Test
    fun editBagClick_editBag_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .perform(ViewActions.replaceText("Bag updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Bag updated"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.no_matches_box_sets_added))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun updateBag_nameInListUpdated() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .perform(ViewActions.replaceText("Bag updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Bag updated"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBagClick_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .check(ViewAssertions.matches(ViewMatchers.withText(bag.name)))

        activityScenario.close()
    }

    @Test
    fun editBagClick_deleteBag() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.no_bags_added))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun bagClick_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(bag.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBag_titleEquals() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.add_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.add_bag))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBag_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.update_bag))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBag_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .check(ViewAssertions.matches(ViewMatchers.withText(bag.name)))

        activityScenario.close()
    }

    @Test
    fun bagsListFragmentClick_bagNameDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        dataSource.insertHistory(history)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToPopular()
        Espresso.onView(ViewMatchers.withId(R.id.bagsListFragment)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(bag.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }
}