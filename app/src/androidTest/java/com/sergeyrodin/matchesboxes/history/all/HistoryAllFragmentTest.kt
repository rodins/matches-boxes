package com.sergeyrodin.matchesboxes.history.all

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.util.Checks
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.*
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
class HistoryAllFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun clearDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun noItems_noItemsTextDisplayed() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()
        dataSource.addHistory()

        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withText(R.string.no_history)).check(matches(isDisplayed()))
    }

    @Test
    fun fewItems_noItemsTexNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        val history3 = History(3, component.id, component.quantity)
        val history4 = History(4, component.id, component.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2, history3, history4)

        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withText(R.string.no_history)).check(matches(not(isDisplayed())))
    }

    @Test
    fun fewItems_componentNameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3, box.id)
        val component2 = RadioComponent(2, "Component2", 4, box.id)
        val component3 = RadioComponent(3, "Component3", 5, box.id)
        val component4 = RadioComponent(4, "Component4", 6, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component3.id, component3.quantity)
        val history4 = History(4, component4.id, component4.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2, component3, component4)
        dataSource.addHistory(history1, history2, history3, history4)

        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withText(component1.name)).check(matches(isDisplayed()))
        onView(withText(component2.name)).check(matches(isDisplayed()))
        onView(withText(component3.name)).check(matches(isDisplayed()))
        onView(withText(component4.name)).check(matches(isDisplayed()))
    }

    @Test
    fun fewItems_fewComponents_componentsNamesDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3, box.id)
        val component2 = RadioComponent(2, "Component2", 4, box.id)
        val component3 = RadioComponent(3, "Component3", 5, box.id)
        val component4 = RadioComponent(4, "Component4", 6, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component3.id, component3.quantity)
        val history4 = History(4, component4.id, component4.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2, component3, component4)
        dataSource.addHistory(history1, history2, history3, history4)

        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withText(component1.name)).check(matches(isDisplayed()))
        onView(withText(component2.name)).check(matches(isDisplayed()))
        onView(withText(component3.name)).check(matches(isDisplayed()))
        onView(withText(component4.name)).check(matches(isDisplayed()))
    }

    @Test
    fun itemClick_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        val history = History(1, component.id, component.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        val scenario = launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //onView(withText(component.name)).perform(click())
        onView(withId(R.id.display_history_list))
            .perform(RecyclerViewActions
                .actionOnItem<DisplayHistoryAdapter.ViewHolder>(hasDescendant(withText(component.name)), click()))

        verify(navController).navigate(
            HistoryAllFragmentDirections.actionHistoryAllFragmentToComponentHistoryFragment(component.id, component.name)
        )
    }

    @Test
    fun longClick_itemHighlighted() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withId(R.id.display_history_list)).check(matches(hasDescendant(hasBackgroundColor(R.color.design_default_color_background))))
        onView(withId(R.id.display_history_list))
            .perform(RecyclerViewActions
                .actionOnItem<DisplayHistoryAdapter.ViewHolder>(hasDescendant(withText(component.name)), longClick()))
        onView(withId(R.id.display_history_list)).check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))
    }

    @Test
    fun clickOnHighlighted_notHighlighted() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withId(R.id.display_history_list))
            .perform(RecyclerViewActions
                .actionOnItem<DisplayHistoryAdapter.ViewHolder>(hasDescendant(withText(component.name)), longClick()))
        onView(withId(R.id.display_history_list))
            .perform(RecyclerViewActions
                .actionOnItem<DisplayHistoryAdapter.ViewHolder>(hasDescendant(withText(component.name)), click()))
        onView(withId(R.id.display_history_list)).check(matches(hasDescendant(hasBackgroundColor(R.color.design_default_color_background))))
    }

    @Test
    fun oneHighlighted_longClickOnSecond_backgroundNotChanged() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withId(R.id.display_history_list))
            .perform(RecyclerViewActions
                .actionOnItem<DisplayHistoryAdapter.ViewHolder>(hasDescendant(withText(component1.name)), longClick()))
        onView(withId(R.id.display_history_list))
            .perform(RecyclerViewActions
                .actionOnItem<DisplayHistoryAdapter.ViewHolder>(hasDescendant(withText(component2.name)), longClick()))

    }

    private fun hasBackgroundColor(colorRes: Int): Matcher<View> {
        Checks.checkNotNull(colorRes)
        return object: TypeSafeMatcher<View>() {

            override fun describeTo(description: Description?) {
                description?.appendText("background color: $colorRes")
            }

            override fun matchesSafely(item: View?): Boolean {
                if(item?.background == null) {
                    return false
                }
                val actualColor = (item.background as ColorDrawable).color
                val expectedColor = ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color
                return actualColor == expectedColor
            }

        }
    }

}