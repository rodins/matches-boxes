package com.sergeyrodin.matchesboxes

import android.widget.AutoCompleteTextView
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.TestModule
import com.sergeyrodin.matchesboxes.history.all.HistoryModelAdapter
import com.sergeyrodin.matchesboxes.history.hasBackgroundColor
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TestModule::class)
class MainActivityHistoryTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dataSource: RadioComponentsDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
        runBlocking {
            dataSource.clearDatabase()
        }
    }

    @Test
    fun noHistory_historyActionClick_noHistoryTextDisplayed() {
        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(R.string.no_history)).check(matches(isDisplayed()))
    }

    @Test
    fun componentQuantityChanged_historyList_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.buttonPlus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        Espresso.pressBack()

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @Test
    fun addComponent_insertHistory_componentNameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
        }

        onView(withId(R.id.neededComponentsFragment)).perform(click())
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText("Component")).check(matches(isDisplayed()))
    }

    @Test
    fun historyItemClick_dateNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015", 1, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity, 1595999582038L)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertHistory(history1)
            dataSource.insertHistory(history2)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(click())

        onView(withText(convertLongToDateString(history2.date)))
            .check(doesNotExist())
    }

    @Test
    fun historyItemClick_titleEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())

        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @Test
    fun componentDetails_historyActionClick_dateDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        moveToSearch()
        typeQuery("78041")
        composeTestRule.onNodeWithText(component.name).performClick()

        onView(withId(R.id.action_history)).perform(click())

        onView(withText(convertLongToDateString(history.date))).check(matches(isDisplayed()))
    }

    @Test
    fun historyItemLongClick_deleteActionDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))
    }

    @Test
    fun historyList_actionDeleteNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withId(R.id.action_delete)).check(doesNotExist())
    }

    @Test
    fun highlightedItemClick_actionDeleteIsNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())
        onView(withText(component.name)).perform(click())

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))
    }

    @Test
    fun exitHistoryAllActionModeByItemClick_navigateBackFromComponentHistory_actionDeleteIsNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())
        onView(withText(component.name)).perform(click())
        onView(withText(component.name)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))
    }

    @Test
    fun exitHistoryAllActionModeByPressBack_navigateBackFromComponentHistory_actionDeleteIsNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())

        longClickOn(component)

        Espresso.pressBack()

        clickOn(component)

        Espresso.pressBack()

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))
    }

    private fun clickOn(component: RadioComponent) {
        onView(withId(R.id.display_history_list))
            .perform(
                RecyclerViewActions
                    .actionOnItem<HistoryModelAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                component.name
                            )
                        ), click()
                    )
            )
    }

    private fun longClickOn(component: RadioComponent) {
        onView(withId(R.id.display_history_list))
            .perform(
                RecyclerViewActions
                    .actionOnItem<HistoryModelAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                component.name
                            )
                        ), longClick()
                    )
            )
    }

    @Test
    fun contextualUpClicked_itemNotHighlighted() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())
        Espresso.pressBack()

        onView(withId(R.id.display_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.design_default_color_background))))
    }

    @Test
    fun actionMode_rotateDevice_actionDeleteDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())

        rotateDeviceToLandscape(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))

        rotateDeviceToPortrait(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))
    }

    @Test
    fun componentHistoryActionMode_rotateDevice_presentationIsHighlighted() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())

        rotateDeviceToLandscape(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.display_component_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))

        rotateDeviceToPortrait(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.display_component_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))
    }

    @Test
    fun historyAllActionMode_rotateDevice_presentationIsHighlighted() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())

        rotateDeviceToLandscape(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.display_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))

        rotateDeviceToPortrait(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.display_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))
    }

    @Test
    fun historyAll_orderDescending() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 5, box.id)
        val component2 = RadioComponent(2, "BUH808", 3, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertHistory(history1)
            dataSource.insertHistory(history2)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withRecyclerView(R.id.display_history_list).atPosition(0))
            .check(matches(hasDescendant(withText(component2.name))))

        onView(withRecyclerView(R.id.display_history_list).atPosition(1))
            .check(matches(hasDescendant(withText(component1.name))))
    }

    private fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    @Test
    fun componentHistoryActionMode_rotateDevice_actionDeleteDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())

        rotateDeviceToLandscape(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))

        rotateDeviceToPortrait(composeTestRule.activity, composeTestRule.activityRule.scenario)

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))
    }

    @Test
    fun notHighlightedItemClick_actionDeleteNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015HI", 4, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertHistory(history1)
            dataSource.insertHistory(history2)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(longClick())
        onView(withText(component2.name)).perform(click())

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))
    }

    @Test
    fun actionDeleteClick_highlightedNameNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015HI", 4, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertHistory(history1)
            dataSource.insertHistory(history2)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())
        onView(withText(component1.name)).check(doesNotExist())
    }

    @Test
    fun deleteHistoryItem_actionDeleteNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015HI", 4, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)

        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertHistory(history1)
            dataSource.insertHistory(history2)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))
    }

    @Test
    fun componentHistoryPresentationLongClick_actionDeleteVisible() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date))).perform(longClick())

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))
    }

    @Test
    fun highlightedComponentHistoryPresentationClick_actionDeleteNotVisible() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date))).perform(longClick())
        onView(withText(convertLongToDateString(history.date))).perform(click())

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))
    }

    @Test
    fun deleteComponentHistory_itemNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history = History(1, component.id, component.quantity)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date))).perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(convertLongToDateString(history.date))).check(doesNotExist())
    }

    @Test
    fun deleteOneOfTwoComponentHistoryPresentations_deletedItemNotVisible() {
        val date = 123456789L
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity, date)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
            dataSource.insertHistory(history1)
            dataSource.insertHistory(history2)
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(convertLongToDateString(history1.date))).perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(convertLongToDateString(history1.date))).check(doesNotExist())
    }

}