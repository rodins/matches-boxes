package com.sergeyrodin.matchesboxes.history.all

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.history.hasBackgroundColor
import com.sergeyrodin.matchesboxes.history.hasBackgroundColorAndText
import org.hamcrest.CoreMatchers.not
import org.junit.After
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

        onView(withText(component.name)).perform(click())

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

        checkIfNotHighlighted()
        performLongClick(component)
        checkIfHighlighted()
    }

    private fun checkIfHighlighted() {
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

        performLongClick(component)
        checkIfHighlighted(component)
        performClick(component)
        checkIfNotHighlighted(component)
    }

    private fun checkIfNotHighlighted() {
        onView(withId(R.id.display_history_list)).check(matches(hasDescendant(hasBackgroundColor(R.color.design_default_color_background))))
    }

    @Test
    fun oneHighlighted_longClickOnSecondItem_backgroundNotChanged() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        performLongClick(component1)
        performLongClick(component2)

        checkIfHighlighted(component1)
        checkIfNotHighlighted(component2)

        performClick(component1) // exit from delete mode
    }

    @Test
    fun clickOnNotHighlightedItem_highlightedItemIsNotHighlighted() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        performLongClick(component1)
        checkIfHighlighted(component1)
        performClick(component2)
        checkIfNotHighlighted(component1)
    }

    private fun checkIfNotHighlighted(component2: RadioComponent) {
        onView(withId(R.id.display_history_list))
            .check(
                matches(
                    hasDescendant(
                        hasBackgroundColorAndText(
                            R.color.design_default_color_background,
                            component2.name
                        )
                    )
                )
            )
    }

    private fun performClick(component: RadioComponent) {
        onView(withId(R.id.display_history_list))
            .perform(
                RecyclerViewActions
                    .actionOnItem<HistoryPresentationAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                component.name
                            )
                        ), click()
                    )
            )
    }

    private fun checkIfHighlighted(component: RadioComponent) {
        onView(withId(R.id.display_history_list))
            .check(
                matches(
                    hasDescendant(
                        hasBackgroundColorAndText(
                            R.color.secondaryLightColor,
                            component.name
                        )
                    )
                )
            )
    }

    private fun performLongClick(component: RadioComponent) {
        onView(withId(R.id.display_history_list))
            .perform(
                RecyclerViewActions
                    .actionOnItem<HistoryPresentationAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                component.name
                            )
                        ), longClick()
                    )
            )
    }

    @Test
    fun actionMode_deleteTextDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        performLongClick(component)

        onView(withText(R.string.delete)).check(matches(isDisplayed()))
    }

    @Test
    fun oneComponent_twoHistories_positiveDeltaDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity+4)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withText("+4")).check(matches(isDisplayed()))
    }

    @Test
    fun oneComponent_twoHistories_negativeDeltaDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity-5)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withText("-5")).check(matches(isDisplayed()))
    }

    @Test
    fun oneComponent_oneHistory_noDeltaDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        launchFragmentInContainer<HistoryAllFragment>(null, R.style.AppTheme)

        onView(withId(R.id.delta_text)).check(matches(withText("")))
    }

    @Test
    fun componentDeleted_highlightedComponentEquals() {
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

        performLongClick(component1)
        onView(withId(R.id.action_delete)).perform(click())
        performLongClick(component2)
        checkIfHighlighted(component2)
    }

}