package com.sergeyrodin.matchesboxes

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.history.hasBackgroundColor
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import com.sergeyrodin.matchesboxes.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
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
    fun componentsAdded_componentQuantityChanged_bagsQuantityEquals() = runBlocking {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        val component9 = RadioComponent(9, "Component9", 9, box5.id)
        val component10 = RadioComponent(10, "Component10", 10, box5.id)
        val component11 = RadioComponent(11, "Component11", 11, box6.id)
        val component12 = RadioComponent(12, "Component12", 12, box6.id)
        val component13 = RadioComponent(13, "Component13", 13, box7.id)
        val component14 = RadioComponent(14, "Component14", 14, box7.id)
        val component15 = RadioComponent(15, "Component15", 15, box8.id)
        val component16 = RadioComponent(16, "Component16", 16, box8.id)
        dataSource.insertBag(bag1)
        dataSource.insertBag(bag2)
        dataSource.insertMatchesBoxSet(set1)
        dataSource.insertMatchesBoxSet(set2)
        dataSource.insertMatchesBoxSet(set3)
        dataSource.insertMatchesBoxSet(set4)
        dataSource.insertMatchesBox(box1)
        dataSource.insertMatchesBox(box2)
        dataSource.insertMatchesBox(box3)
        dataSource.insertMatchesBox(box4)
        dataSource.insertMatchesBox(box5)
        dataSource.insertMatchesBox(box6)
        dataSource.insertMatchesBox(box7)
        dataSource.insertMatchesBox(box8)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertRadioComponent(component3)
        dataSource.insertRadioComponent(component4)
        dataSource.insertRadioComponent(component5)
        dataSource.insertRadioComponent(component6)
        dataSource.insertRadioComponent(component7)
        dataSource.insertRadioComponent(component8)
        dataSource.insertRadioComponent(component9)
        dataSource.insertRadioComponent(component10)
        dataSource.insertRadioComponent(component11)
        dataSource.insertRadioComponent(component12)
        dataSource.insertRadioComponent(component13)
        dataSource.insertRadioComponent(component14)
        dataSource.insertRadioComponent(component15)
        dataSource.insertRadioComponent(component16)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.button_plus)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withText(bag2.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set3.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box5.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component9.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.button_plus)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withText("37"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("101"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

}