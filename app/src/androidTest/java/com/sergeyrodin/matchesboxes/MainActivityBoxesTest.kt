package com.sergeyrodin.matchesboxes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.di.TestModule
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
class MainActivityBoxesTest {

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
    fun addMatchesBox_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()
        onView(withId(R.id.box_edit)).perform(typeText("Box"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        composeTestRule.onNodeWithText("Box").assertIsDisplayed()
    }

    @Test
    fun addTwoBoxes_namesEqual() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()
        onView(withId(R.id.box_edit))
            .perform(typeText("Box"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        composeTestRule.onNodeWithText("Box").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()
        onView(withId(R.id.box_edit))
            .perform(typeText("Box2"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        composeTestRule.onNodeWithText("Box2").assertIsDisplayed()
    }

    @Test
    fun addMatchesBox_hideDeleteButton() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()

        onView(withId(R.id.action_delete)).check(doesNotExist())
    }

    @Test
    fun updateBox_nameEquals() {
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
        onView(withId(R.id.box_edit)).perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        composeTestRule.onNodeWithText("Updated box").assertIsDisplayed()
    }

    @Test
    fun boxClicked_titleEquals() {
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
        onView(withText(box.name)).check(matches(isDisplayed()))
    }

    @Test
    fun addBox_titleEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()
        onView(withText(R.string.add_box)).check(matches(isDisplayed()))
    }

    @Test
    fun editBox_titleEquals() {
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
        onView(withText(R.string.update_box)).check(matches(isDisplayed()))
    }

    @Test
    fun editBox_nameEquals() {
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
        onView(withId(R.id.box_edit)).check(matches(withText(box.name)))
    }

    @Test
    fun updateBox_titleEquals() {
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
        onView(withId(R.id.box_edit)).perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withText("Updated box")).check(matches(isDisplayed()))
    }

    @Test
    fun deleteBox_setTitleDisplayed() {
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

        onView(withText(set.name)).check(matches(isDisplayed()))
    }
}
