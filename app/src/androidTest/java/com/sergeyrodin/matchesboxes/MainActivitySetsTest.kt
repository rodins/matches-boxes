package com.sergeyrodin.matchesboxes

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.di.TestModule
import com.sergeyrodin.matchesboxes.nametextfield.NAME_TEXT_FIELD_TAG
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TestModule::class)
class MainActivitySetsTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dataSource: RadioComponentsDataSource

    private var addSetDescription = ""
    private var emptyText = ""

    @Before
    fun initDataSource() {
        hiltRule.inject()
        addSetDescription = composeTestRule.activity.getString(R.string.add_set)
        emptyText = composeTestRule.activity.getString(R.string.no_matches_box_sets_added)
        runBlocking {
            dataSource.clearDatabase()
        }
    }

    @Test
    fun addMatchesBoxSet_nameMatches() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()

        composeTestRule.onNodeWithText(emptyText).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("MBS1")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        onView(withText("Bag")).check(matches(isDisplayed()))
        composeTestRule.onNodeWithText("MBS1").assertIsDisplayed()
    }

    @Test
    fun addTwoSets_namesEqual() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()

        composeTestRule.onNodeWithText(emptyText).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("Set")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        composeTestRule.onNodeWithText("Set").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("Set2")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        composeTestRule.onNodeWithText("Set2").assertIsDisplayed()
    }

    @Test
    fun addSet_deleteButtonNotDisplayed() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()
        onView(withId(R.id.action_delete)).check(doesNotExist())
    }

    @Test
    fun updateSet_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Set updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        onView(withText("Set updated")).check(matches(isDisplayed()))
        composeTestRule.onNodeWithTextResource(R.string.no_matches_boxes_added).assertIsDisplayed()
    }

    @Test
    fun updateSet_nameInListUpdated() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Set updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithText("Set updated").assertIsDisplayed()
    }

    @Test
    fun setClick_titleEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withText(set.name)).check(matches(isDisplayed()))
    }

    @Test
    fun addSet_titleEquals() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()
        onView(withText(R.string.add_set)).check(matches(isDisplayed()))
    }

    @Test
    fun editSet_titleEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withText(R.string.update_set)).perform(click())
    }

    @Test
    fun editSet_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        composeTestRule.onNodeWithText(set.name).assertIsDisplayed()
    }

    @Test
    fun setDelete_bagTitleDisplayed() {
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

        onView(withText(bag.name)).check(matches(isDisplayed()))
    }
}