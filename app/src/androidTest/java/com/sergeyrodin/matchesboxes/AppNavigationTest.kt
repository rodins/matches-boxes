package com.sergeyrodin.matchesboxes

import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
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
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TestModule::class)
class AppNavigationTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dataSource: RadioComponentsDataSource

    private var addBagDescription = ""
    private var addSetDescription = ""
    private var emptyBagsText = ""
    private var emptySetsText = ""

    @Before
    fun initDataSource() {
        hiltRule.inject()
        addBagDescription = composeTestRule.activity.getString(R.string.add_bag)
        addSetDescription = composeTestRule.activity.getString(R.string.add_set)
        emptyBagsText = composeTestRule.activity.getString(R.string.no_bags_added)
        emptySetsText = composeTestRule.activity.getString(R.string.no_matches_box_sets_added)
        runBlocking {
            dataSource.clearDatabase()
        }
    }

    // Bag

    @Test
    fun addBag_navigateBack() {
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))
        pressBack()
        composeTestRule.onNodeWithText(emptyBagsText).assertIsDisplayed()
    }

    @Test
    fun addBag_navigateUp() {
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        composeTestRule.onNodeWithText(emptyBagsText).assertIsDisplayed()
    }

    @Test
    fun addBagSaved_navigationBack() {
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"), closeSoftKeyboard())
        onView(withId(R.id.save_bag_fab)).perform(click())

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }
    }

    @Test
    fun editBagSaved_navigationBack() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    @Test
    fun editBagSaved_navigationUp() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    @Test
    fun deleteBag_navigationBack() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform((click()))

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }
    }

    @Test
    fun mainScreen_navigateBack() {
        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }
    }

    // Set

    @Test
    fun addSet_navigateBack() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()

        pressBack()

        composeTestRule.onNodeWithText(emptySetsText).assertIsDisplayed()
    }

    @Test
    fun addSet_navigateUp() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithText(emptySetsText).assertIsDisplayed()
    }

    @Test
    fun addSetSaved_navigateBack() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()
        onView(withId(R.id.set_edit)).perform(typeText("New set"), closeSoftKeyboard())
        onView(withId(R.id.save_set_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    @Test
    fun addSetSaved_navigateUp() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()
        onView(withId(R.id.set_edit)).perform(typeText("New set"), closeSoftKeyboard())
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    @Test
    fun editSetSaved_navigationBack() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).perform(replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
    }

    @Test
    fun editSetSaved_navigationUp() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).perform(replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
    }

    @Test
    fun setDeleted_navigationBack() {
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

        pressBack()

        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    @Test
    fun setDeleted_navigationUp() {
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

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    // Box
    @Test
    fun addBoxSaved_navigateBack() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
    }

    @Test
    fun addBoxSaved_navigateUp() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
    }

    @Test
    fun editBoxSaved_navigationBack() {
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
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(replaceText("Box updated"))
        onView(withId(R.id.save_box_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun editBoxSaved_navigationUp() {
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
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(replaceText("Box updated"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteBox_navigationBack() {
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
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
    }

    @Test
    fun deleteBox_navigationUp() {
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
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
    }

    // Component

    @Test
    fun addComponentSaved_navigateBack() {
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
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("New component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun addComponentSaved_navigateUp() {
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
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("New component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun editComponentSaved_navigateBack() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun editComponentSaved_navigateBackAllLevels() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()
        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
        pressBack()
        composeTestRule.onNodeWithContentDescription(addSetDescription).assertIsDisplayed()
        pressBack()
        composeTestRule.onNodeWithContentDescription(addBagDescription).assertIsDisplayed()
    }

    @Test
    fun editComponentSaved_navigateUp() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteComponent_navigationBack() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteComponent_navigationUp() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        composeTestRule.onNodeWithText(set.name).performClick()
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
    }

    // Search

    @SearchTest
    @Test
    fun searchEdit_navigationBack() {
        val bag = Bag(1, "Bag")
        val bag2 = Bag(2, "Bag2")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertBag(bag2)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertRadioComponent(component3)
        }

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.quantity_edit)).perform(replaceText("3"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithText(bag2.name).assertIsDisplayed()
    }

    @SearchTest
    @Test
    fun searchEdit_navigationUp() {
        val bag = Bag(1, "Bag")
        val bag2 = Bag(2, "Bag2")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertBag(bag2)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertRadioComponent(component3)
        }

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.quantity_edit)).perform(replaceText("3"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithText(bag2.name).assertIsDisplayed()
    }

    @SearchTest
    @Test
    fun searchMode_addComponent_navigationUp() {
        val bag = Bag(1, "Bag")
        val bag2 = Bag(2, "Bag2")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertBag(bag2)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertRadioComponent(component3)
        }

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withId(R.id.add_search_buy_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("STRW6753"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithText(bag2.name).assertIsDisplayed()
    }

    @SearchTest
    @Test
    fun searchMode_addComponent_navigationBack() {
        val bag = Bag(1, "Bag")
        val bag2 = Bag(2, "Bag2")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertBag(bag2)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component1)
            dataSource.insertRadioComponent(component2)
            dataSource.insertRadioComponent(component3)
        }

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withId(R.id.add_search_buy_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("STRW6753"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithText(bag2.name).assertIsDisplayed()
    }

    @SearchTest
    @Test
    fun typeQueryInSearchFragment_navigateToDetailsAndBack_nameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        onView(withId(R.id.searchFragment)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78"))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))
        onView(withText(component.name)).perform(click())

        pressBack()

        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @SearchTest
    @Test
    fun typeQueryInSearchFragment_navigateToDetailsAndUp_nameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 3, box.id)
        runBlocking {
            dataSource.insertBag(bag)
            dataSource.insertMatchesBoxSet(set)
            dataSource.insertMatchesBox(box)
            dataSource.insertRadioComponent(component)
        }

        onView(withId(R.id.searchFragment)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78"))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))
        onView(withText(component.name)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNavigationDisplayed() {
        onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNavigationBagsListFragmentDisplayed() {
        onView(withId(R.id.bagsListFragment)).check(matches(isDisplayed()))
    }
}
