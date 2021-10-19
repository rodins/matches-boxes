package com.sergeyrodin.matchesboxes

import android.app.Activity
import android.widget.AutoCompleteTextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
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
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
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
class MainActivityHistoryTest {

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
    fun noHistory_historyActionClick_noHistoryTextDisplayed() = runBlocking{
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withText(R.string.no_history)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun componentQuantityChanged_historyList_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.buttonPlus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        Espresso.pressBack()

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withText(component.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addComponent_insertHistory_componentNameDisplayed() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)


        onView(withId(R.id.neededComponentsFragment))
            .perform(click())
        onView(withId(R.id.add_needed_component_fab))
            .perform(click())
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText("Component"))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun historyItemClick_dateNotDisplayed() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015", 1, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity, 1595999582038L)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertHistory(history1)
        dataSource.insertHistory(history2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(click())

        onView(withText(convertLongToDateString(history2.date)))
            .check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun historyItemClick_titleEquals() = runBlocking {
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())

        onView(withText(component.name))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun componentDetails_historyActionClick_dateDisplayed() = runBlocking {
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

        moveToSearch()
        typeQuery("78041")
        onView(withText(component.name)).perform(click())

        onView(withId(R.id.action_history)).perform(click())

        onView(withText(convertLongToDateString(history.date)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun historyItemLongClick_deleteActionDisplayed() = runBlocking{
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())

        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun historyList_actionDeleteNotDisplayed() = runBlocking{
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

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withId(R.id.action_delete))
            .check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun highlightedItemClick_actionDeleteIsNotDisplayed() = runBlocking {
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())
        onView(withText(component.name)).perform(click())

        onView(withId(R.id.action_delete))
            .check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun exitHistoryAllActionModeByItemClick_navigateBackFromComponentHistory_actionDeleteIsNotDisplayed() = runBlocking {
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())
        onView(withText(component.name)).perform(click())
        onView(withText(component.name)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.action_delete))
            .check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun exitHistoryAllActionModeByPressBack_navigateBackFromComponentHistory_actionDeleteIsNotDisplayed() = runBlocking {
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

        onView(withId(R.id.historyAllFragment)).perform(click())

        longClickOn(component)

        Espresso.pressBack()

        clickOn(component)

        Espresso.pressBack()

        onView(withId(R.id.action_delete)).check(matches(not(isDisplayed())))

        activityScenario.close()
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
    fun contextualUpClicked_itemNotHighlighted() = runBlocking {
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())
        Espresso.pressBack()

        onView(withId(R.id.display_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.design_default_color_background))))

        activityScenario.close()
    }

    @Test
    fun actionMode_rotateDevice_actionDeleteDisplayed() = runBlocking {
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
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())

        rotateDeviceToLandscape(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))

        rotateDeviceToPortrait(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun componentHistoryActionMode_rotateDevice_presentationIsHighlighted() = runBlocking {
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
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())

        rotateDeviceToLandscape(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.display_component_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))

        rotateDeviceToPortrait(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.display_component_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))

        activityScenario.close()
    }

    @Test
    fun historyAllActionMode_rotateDevice_presentationIsHighlighted() = runBlocking{
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
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(longClick())

        rotateDeviceToLandscape(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.display_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))

        rotateDeviceToPortrait(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.display_history_list))
            .check(matches(hasDescendant(hasBackgroundColor(R.color.secondaryLightColor))))

        activityScenario.close()
    }

    @Test
    fun historyAll_orderDescending() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 5, box.id)
        val component2 = RadioComponent(2, "BUH808", 3, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertHistory(history1)
        dataSource.insertHistory(history2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())

        onView(withRecyclerView(R.id.display_history_list).atPosition(0))
            .check(matches(hasDescendant(withText(component2.name))))

        onView(withRecyclerView(R.id.display_history_list).atPosition(1))
            .check(matches(hasDescendant(withText(component1.name))))

        activityScenario.close()
    }

    private fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    @Test
    fun componentHistoryActionMode_rotateDevice_actionDeleteDisplayed() = runBlocking{
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
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())

        rotateDeviceToLandscape(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))

        rotateDeviceToPortrait(activity, activityScenario, dataBindingIdlingResource)

        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun notHighlightedItemClick_actionDeleteNotDisplayed() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015HI", 4, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertHistory(history1)
        dataSource.insertHistory(history2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(longClick())
        onView(withText(component2.name)).perform(click())

        onView(withId(R.id.action_delete))
            .check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun actionDeleteClick_highlightedNameNotDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015HI", 4, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertHistory(history1)
        dataSource.insertHistory(history2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())
        onView(withText(component1.name)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun deleteHistoryItem_actionDeleteNotDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015HI", 4, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertHistory(history1)
        dataSource.insertHistory(history2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component1.name)).perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withId(R.id.action_delete))
            .check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun componentHistoryPresentationLongClick_actionDeleteVisible() = runBlocking {
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())

        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun highlightedComponentHistoryPresentationClick_actionDeleteNotVisible() = runBlocking{
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())
        onView(withText(convertLongToDateString(history.date)))
            .perform(click())

        onView(withId(R.id.action_delete))
            .check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun deleteComponentHistory_itemNotDisplayed() = runBlocking{
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

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(convertLongToDateString(history.date)))
            .perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(convertLongToDateString(history.date)))
            .check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun deleteOneOfTwoComponentHistoryPresentations_deletedItemNotVisible() = runBlocking {
        val date = 123456789L
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity, date)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        dataSource.insertHistory(history1)
        dataSource.insertHistory(history2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.historyAllFragment)).perform(click())
        onView(withText(convertLongToDateString(history1.date)))
            .perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(convertLongToDateString(history1.date)))
            .check(doesNotExist())

        activityScenario.close()
    }

}