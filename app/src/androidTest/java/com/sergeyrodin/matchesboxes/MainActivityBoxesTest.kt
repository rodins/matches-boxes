package com.sergeyrodin.matchesboxes

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityBoxesTest {

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
    fun addMatchesBox_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit)).perform(ViewActions.typeText("Box"))
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Box"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addTwoBoxes_namesEqual() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit))
            .perform(ViewActions.typeText("Box"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Box"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit))
            .perform(ViewActions.typeText("Box2"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Box2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addMatchesBox_hideDeleteButton() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.action_delete))
            .check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @Test
    fun updateBox_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit))
            .perform(ViewActions.replaceText("Updated box"))
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Updated box"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun boxClicked_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBox_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.add_box))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBox_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.update_box))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBox_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit))
            .check(ViewAssertions.matches(ViewMatchers.withText(box.name)))

        activityScenario.close()
    }

    @Test
    fun updateBox_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit))
            .perform(ViewActions.replaceText("Updated box"))
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Updated box"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteBox_setTitleDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(set.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }
}