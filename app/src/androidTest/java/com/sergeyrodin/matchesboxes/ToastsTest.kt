package com.sergeyrodin.matchesboxes

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.TestModule
import com.sergeyrodin.matchesboxes.nametextfield.NAME_TEXT_FIELD_TAG
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
class ToastsTest {

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

    // Bag

    @Test
    fun addBag_showToast() {
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).performClick()

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New bag")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        checkToastIsDisplayed(R.string.bag_added)
    }

    private fun checkToastIsDisplayed(stringId: Int) {
        val decorView = composeTestRule.activity.window.decorView
        onView(withText(stringId))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun editBag_toastShow() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        onView(withId(R.id.action_edit)).perform(click())

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Bag updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        checkToastIsDisplayed(R.string.bag_updated)
    }

    @Test
    fun deleteBag_toastShow() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(R.string.bag_deleted)
    }

    // Set

    @Test
    fun addSet_showToast() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).performClick()
        onView(withId(R.id.set_edit)).perform(typeText("MBS1"), closeSoftKeyboard())
        onView(withId(R.id.save_set_fab)).perform(click())

        checkToastIsDisplayed(R.string.matches_box_set_added)
    }

    @Test
    fun updateSet_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit))
            .perform(replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        checkToastIsDisplayed(R.string.matches_box_set_updated)
    }

    @Test
    fun deleteSet_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(R.string.matches_box_set_deleted)
    }

    // Box

    @Test
    fun addMatchesBox_toastShown() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()

        onView(withId(R.id.box_edit)).perform(typeText("New box"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        checkToastIsDisplayed(R.string.box_added)
    }

    @Test
    fun updateBox_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithText(box.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit))
            .perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        checkToastIsDisplayed(R.string.box_updated)
    }

    @Test
    fun deleteBox_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithText(box.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(R.string.box_deleted)
    }

    // Component

    @Test
    fun addComponent_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        checkToastIsDisplayed(R.string.component_added)
    }

    @Test
    fun addComponent_emptyName_showErrorToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
        onView(withId(R.id.save_component_fab)).perform(click())

        checkToastIsDisplayed(R.string.save_component_error)
    }

    @Test
    fun updateComponent_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())

        onView(withId(R.id.component_edit))
            .perform(typeText("Component updated"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        checkToastIsDisplayed(R.string.component_updated)
    }

    @Test
    fun deleteComponent_showToast() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())

        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(R.string.component_deleted)
    }
}
