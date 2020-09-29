package com.sergeyrodin.matchesboxes.history.component

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.history.hasBackgroundColor
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComponentHistoryFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun resetDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun noItems_noItemsTextVisible() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory()
        val bundle = ComponentHistoryFragmentArgs.Builder(component.id, component.name).build().toBundle()
        launchFragmentInContainer<ComponentHistoryFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_history)).check(matches(isDisplayed()))
    }

    @Test
    fun oneItem_noItemsTextNotVisible() {
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
        val bundle = ComponentHistoryFragmentArgs.Builder(component.id, component.name).build().toBundle()
        launchFragmentInContainer<ComponentHistoryFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_history)).check(matches(not(isDisplayed())))
    }

    @Test
    fun oneItem_dateEquals() {
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
        val bundle = ComponentHistoryFragmentArgs.Builder(component.id, component.name).build().toBundle()
        launchFragmentInContainer<ComponentHistoryFragment>(bundle, R.style.AppTheme)

        onView(withText(convertLongToDateString(history.date))).check(matches(isDisplayed()))
    }

    @Test
    fun longClick_itemHighlighted() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        val bundle = ComponentHistoryFragmentArgs.Builder(component.id, component.name).build().toBundle()
        launchFragmentInContainer<ComponentHistoryFragment>(bundle, R.style.AppTheme)

        presentationLongClick(history)

        checkIfListHasHighlightedElement()
    }

    private fun presentationLongClick(history: History) {
        onView(withId(R.id.display_component_history_list))
            .perform(
                RecyclerViewActions
                    .actionOnItem<DisplayComponentHistoryAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                convertLongToDateString(history.date)
                            )
                        ), longClick()
                    )
            )
    }

    private fun checkIfListHasHighlightedElement() {
        onView(withId(R.id.display_component_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))
    }



    @Test
    fun presentationClickOnHighlightedItem_itemIsNotHighlighted() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        val bundle = ComponentHistoryFragmentArgs.Builder(component.id, component.name).build().toBundle()
        launchFragmentInContainer<ComponentHistoryFragment>(bundle, R.style.AppTheme)

        presentationLongClick(history)

        presentationClick(history)

        checkIfListHasNotHighlightedElement()
    }

    private fun checkIfListHasNotHighlightedElement() {
        onView(withId(R.id.display_component_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.design_default_color_background))))
    }

    private fun presentationClick(history: History) {
        onView(withId(R.id.display_component_history_list))
            .perform(
                RecyclerViewActions
                    .actionOnItem<DisplayComponentHistoryAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                convertLongToDateString(history.date)
                            )
                        ), click()
                    )
            )
    }
}