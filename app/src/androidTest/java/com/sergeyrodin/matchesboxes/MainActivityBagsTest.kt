package com.sergeyrodin.matchesboxes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
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
class MainActivityBagsTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dataSource: RadioComponentsDataSource

    private var addBagDescription = ""
    private var emptyText = ""

    @Before
    fun initDataSource() {
        hiltRule.inject()
        addBagDescription = composeTestRule.activity.getString(R.string.add_bag)
        emptyText = composeTestRule.activity.getString(R.string.no_bags_added)
        runBlocking {
            dataSource.clearDatabase()
        }
    }

    @Test
    fun addBag_bagNameDisplayed() {
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"), closeSoftKeyboard())
        onView(withId(R.id.save_bag_fab)).perform(click())
        composeTestRule.onNodeWithText("New bag").assertIsDisplayed()
    }

    @Test
    fun addBag_deleteButtonIsNotDisplayed() {
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withId(R.id.action_delete)).check(doesNotExist())
    }

    @Test
    fun noBags_addBagClick_textEquals() {
        composeTestRule.onNodeWithText(emptyText).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))
    }

    @Test
    fun editBagClick_editBag_nameEquals() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())

        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())
        onView(withText("Bag updated")).check(matches(isDisplayed()))
        val setsEmptyText = composeTestRule.activity.getString(R.string.no_matches_box_sets_added)
        composeTestRule.onNodeWithText(setsEmptyText).assertIsDisplayed()
    }

    @Test
    fun updateBag_nameInListUpdated() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())

        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithText("Bag updated").assertIsDisplayed()
    }

    @Test
    fun editBagClick_nameEquals() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())

        onView(withId(R.id.bag_edit)).check(matches(withText(bag.name)))
    }

    @Test
    fun editBagClick_deleteBag() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())
        composeTestRule.onNodeWithText(emptyText).assertIsDisplayed()
    }

    @Test
    fun bagClick_titleEquals() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withText(bag.name)).check(matches(isDisplayed()))
    }

    @Test
    fun addBag_titleEquals() {
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withText(R.string.add_bag)).check(matches(isDisplayed()))
    }

    @Test
    fun editBag_titleEquals() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withText(R.string.update_bag)).check(matches(isDisplayed()))
    }

    @Test
    fun editBag_nameEquals() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withText(bag.name)))
    }

    @Test
    fun bagsListFragmentClick_bagNameDisplayed() {
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

        moveToPopular()
        onView(withId(R.id.bagsListFragment)).perform(click())
        composeTestRule.onNodeWithText(bag.name).assertIsDisplayed()
    }
}
