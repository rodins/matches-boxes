package com.sergeyrodin.matchesboxes

import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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

    @Before
    fun initDataSource() {
        hiltRule.inject()
        runBlocking {
            dataSource.clearDatabase()
        }
    }

    // Bag

    @Test
    fun addBag_navigateBack() {
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).performClick()
        composeTestRule.onNodeWithTextResource(R.string.enter_bag_name).assertIsDisplayed()
        pressBack()

        composeTestRule.onNodeWithTextResource(R.string.no_bags_added).assertIsDisplayed()
    }

    @Test
    fun addBag_navigateUp() {
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).performClick()
        composeTestRule.onNodeWithTextResource(R.string.enter_bag_name).assertIsDisplayed()
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        composeTestRule.onNodeWithTextResource(R.string.no_bags_added).assertIsDisplayed()
    }

    @Test
    fun addBagSaved_navigationBack() {
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).performClick()

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New bag")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

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
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Bag updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
    }

    @Test
    fun editBagSaved_navigationUp() {
        val bag = Bag(1, "Bag")
        runBlocking {
            dataSource.insertBag(bag)
        }

        composeTestRule.onNodeWithText(bag.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Bag updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
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
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).performClick()

        pressBack()

        composeTestRule.onNodeWithTextResource(R.string.no_matches_box_sets_added).assertIsDisplayed()
    }

    @Test
    fun addSet_navigateUp() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).performClick()

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithTextResource(R.string.no_matches_box_sets_added).assertIsDisplayed()
    }

    @Test
    fun addSetSaved_navigateBack() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).performClick()
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New set")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
    }

    @Test
    fun addSetSaved_navigateUp() {
        runBlocking {
            dataSource.insertBag(Bag(1, "Bag"))
        }

        composeTestRule.onNodeWithText("Bag").performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).performClick()
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New set")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
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
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Set updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
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
        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement("Set updated")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
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

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
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

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
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
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()
        onView(withId(R.id.box_edit)).perform(typeText("New box"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
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
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()
        onView(withId(R.id.box_edit)).perform(typeText("New box"), closeSoftKeyboard())
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(replaceText("Box updated"))
        onView(withId(R.id.save_box_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(replaceText("Box updated"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
        onView(withId(R.id.component_edit)).perform(typeText("New component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
        onView(withId(R.id.component_edit)).perform(typeText("New component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
        pressBack()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_set).assertIsDisplayed()
        pressBack()
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_bag).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(box.name).performClick()
        composeTestRule.onNodeWithText(component.name).performClick()
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(component3.name).performClick()
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
        composeTestRule.onNodeWithText(component3.name).performClick()
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
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
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
        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()
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
        composeTestRule.onNodeWithText(component.name).performClick()

        pressBack()

        composeTestRule.onNodeWithText(component.name).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(component.name).performClick()

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        composeTestRule.onNodeWithText(component.name).assertIsDisplayed()
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
