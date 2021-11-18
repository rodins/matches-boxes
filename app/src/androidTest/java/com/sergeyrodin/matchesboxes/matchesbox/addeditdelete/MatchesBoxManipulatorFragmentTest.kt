package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.nametextfield.NAME_TEXT_FIELD_TAG
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class MatchesBoxManipulatorFragmentTest{

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @get:Rule(order = 3)
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun addItem_hintDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxes(MatchesBox(1, "Box", set.id))
        val bundle = MatchesBoxManipulatorFragmentArgs.Builder(set.id, ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragment<MatchesBoxManipulatorFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTextResource(R.string.enter_box_name).assertIsDisplayed()
    }

    @Test
    fun itemIdArg_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        val bundle = MatchesBoxManipulatorFragmentArgs.Builder(set.id, box.id, "Title").build().toBundle()
        launchFragment<MatchesBoxManipulatorFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithText(box.name).assertIsDisplayed()
    }

    @Test
    fun addItem_saveName_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes()

        val navController = Mockito.mock(NavController::class.java)

        val bundle = MatchesBoxManipulatorFragmentArgs.Builder(set.id, ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragment<MatchesBoxManipulatorFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New box")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        verify(navController).navigate(
            MatchesBoxManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(set.id, set.name)
        )
    }

    @Test
    fun updateBox_navigationCalled() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val updatedBox = MatchesBox(1, "Updated box", set.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box.copy())

        val navController = Mockito.mock(NavController::class.java)

        val bundle = MatchesBoxManipulatorFragmentArgs.Builder(set.id, box.id, "Title").build().toBundle()
        launchFragment<MatchesBoxManipulatorFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement(updatedBox.name)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        verify(navController).navigate(
            MatchesBoxManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToRadioComponentsListFragment(updatedBox.id, updatedBox.name)
        )
    }

    @Test
    fun deleteItem_menuItemClick_sizeZero() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = MatchesBoxManipulatorFragmentArgs.Builder(set.id, box.id, "Title").build().toBundle()
        launchFragment<MatchesBoxManipulatorFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
            clickDeleteAction(this)
        }

        val items = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
        assertThat(items.size, `is`(0))

        verify(navController).navigate(
            MatchesBoxManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(set.id, set.name)
        )
    }

    private fun clickDeleteAction(
        fragment: Fragment
    ) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        fragment.onOptionsItemSelected(deleteMenuItem)
    }
}