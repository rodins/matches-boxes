package com.sergeyrodin.matchesboxes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
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
class MainActivityComponentsTest {

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
    fun selectComponent_detailsDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 4, box.id, true)

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

        onView(withText(bag.name)).check(matches(isDisplayed()))
        onView(withText(set.name)).check(matches(isDisplayed()))
        onView(withText(box.name)).check(matches(isDisplayed()))
        onView(withText(component.name)).check(matches(isDisplayed()))
        onView(withText(component.quantity.toString())).check(matches(isDisplayed()))
        onView(withId(R.id.buy_checkbox)).check(matches(isChecked()))
    }

    @Test
    fun detailsFragment_editFabClick_namesDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 4, box.id, true)

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

        onView(withText(bag.name)).check(matches(isDisplayed()))
        onView(withText(set.name)).check(matches(isDisplayed()))
        onView(withText(box.name)).check(matches(isDisplayed()))
        onView(withText(component.name)).check(matches(isDisplayed()))
        onView(withText(component.quantity.toString())).check(matches(isDisplayed()))
        onView(withId(R.id.buy_checkbox)).check(matches(isChecked()))
    }

    @Test
    fun componentUpdated_boxNameEquals() {
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
        val component = RadioComponent(1, "Component", 4, box2.id)
        runBlocking {
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
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag1.name).performClick()
        composeTestRule.onNodeWithText(set1.name).performClick()
        composeTestRule.onNodeWithText(box2.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withText(bag1.name)).perform(click())
        onView(withText(bag2.name)).perform(click())
        onView(withText(set3.name)).perform(click())
        onView(withText(set4.name)).perform(click())
        onView(withText(box7.name)).perform(click())
        onView(withText(box8.name)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())
        composeTestRule.onNodeWithText(component.name).performClick()

        onView(withText(box8.name)).check(matches(isDisplayed()))
    }

    @Test
    fun boxChanged_componentAdded_nameDisplayed() {
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
        runBlocking {
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
        }

        composeTestRule.onNodeWithText(bag1.name).performClick()
        composeTestRule.onNodeWithText(set1.name).performClick()
        composeTestRule.onNodeWithText(box2.name).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withText(bag1.name)).perform(click())
        onView(withText(bag2.name)).perform(click())
        onView(withText(set3.name)).perform(click())
        onView(withText(set4.name)).perform(click())
        onView(withText(box7.name)).perform(click())
        onView(withText(box8.name)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())
        composeTestRule.onNodeWithText("Component").performClick()

        onView(withText(box8.name)).check(matches(isDisplayed()))
    }

    @Test
    fun addComponent_nameEquals() {
        // Add bag
        composeTestRule.onNodeWithContentDescription(R.string.add_bag).performClick()
        onView(withId(R.id.bag_edit)).perform(typeText("Bag"), closeSoftKeyboard())
        onView(withId(R.id.save_bag_fab)).perform(click())

        // Add matches box set
        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescription(R.string.add_set).performClick()
        onView(withId(R.id.set_edit)).perform(typeText("Set"), closeSoftKeyboard())
        onView(withId(R.id.save_set_fab)).perform(click())

        // Add matches box
        composeTestRule.onNodeWithText("Set").performClick()
        composeTestRule.onNodeWithContentDescription(R.string.add_box).performClick()
        onView(withId(R.id.box_edit)).perform(typeText("Box"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        // Add component
        composeTestRule.onNodeWithText("Box").performClick()
        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        composeTestRule.onNodeWithContentDescription(R.string.add_component).assertIsDisplayed()
        composeTestRule.onNodeWithText("Component").assertIsDisplayed()
    }

    @Test
    fun addTwoComponents_namesEqual() {
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

        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component2"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        composeTestRule.onNodeWithText("Component").assertIsDisplayed()
        composeTestRule.onNodeWithText("Component2").assertIsDisplayed()
    }

    @Test
    fun addComponent_deleteButtonVisible() {
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
        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withId(R.id.action_delete)).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun updateComponent_deleteButtonVisible() {
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
        onView(withId(R.id.action_delete))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addComponent_titleEquals() {
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
        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withText(R.string.add_component)).check(matches(isDisplayed()))
    }

    @Test
    fun addComponent_checkBuy_buyChecked() {
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
        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.buy_checkbox)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())
        composeTestRule.onNodeWithText("Component").performClick()

        onView(withId(R.id.buy_checkbox)).check(matches(isChecked()))
    }

    @Test
    fun editComponent_titleEquals() {
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
        onView(withText(R.string.update_component)).check(matches(isDisplayed()))
    }

    @Test
    fun updateComponent_nameInListUpdated() {
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
            .perform(replaceText("Component updated"))
        onView(withId(R.id.save_component_fab)).perform(click())

        composeTestRule.onNodeWithText("Component updated").assertIsDisplayed()
    }

    @Test
    fun editComponent_nameEquals() {
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
        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteQuantity_quantityZero() {
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
        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()
        onView(withId(R.id.component_edit))
            .perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.quantity_edit))
            .perform(replaceText(""))
        onView(withId(R.id.save_component_fab)).perform(click())
        composeTestRule.onNodeWithText("Component").performClick()

        onView(withText("0")).check(matches(isDisplayed()))
    }
}
