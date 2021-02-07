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
class MainActivityBuyTest {

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
    fun buyClick_nameDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id, true)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertRadioComponent(component3)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.neededComponentsFragment))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component3.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun buyClick_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.neededComponentsFragment))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.buy_components))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun buyMode_buyChanged_nameNotDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id, true)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.neededComponentsFragment))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.buy_checkbox)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(component1.name)).check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @Test
    fun buyComponent_changeQuantity_quantityEquals() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id, true)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.neededComponentsFragment))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.button_plus)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.buy_components))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun buyMode_deleteComponent_navigateToSearchComponents() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id, true)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.neededComponentsFragment))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.buy_components))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

}