package com.sergeyrodin.matchesboxes

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
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
class MainActivityPopularTest {

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
        dataSource =
            ServiceLocator.provideRadioComponentsDataSource(ApplicationProvider.getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun navdrawerPopularClick_componentNameDisplayed() = runBlocking {
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

        Espresso.onView(ViewMatchers.withText(component.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun navdrawerPopularClick_titleDisplayed() = runBlocking{
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

        Espresso.onView(ViewMatchers.withText(R.string.popular_components))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }



}