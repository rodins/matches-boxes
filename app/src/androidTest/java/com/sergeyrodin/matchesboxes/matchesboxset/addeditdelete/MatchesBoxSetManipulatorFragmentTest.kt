package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.nametextfield.NAME_TEXT_FIELD_TAG
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
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
class MatchesBoxSetManipulatorFragmentTest {

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
    fun addNewSet_hintDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "MBS1", bag.id)
        dataSource.addMatchesBoxSets(set)
        val bundle =
            MatchesBoxSetManipulatorFragmentArgs.Builder(bag.id, ADD_NEW_ITEM_ID, "Title").build()
                .toBundle()
        launchFragment<MatchesBoxSetManipulatorFragment>(
            composeTestRule.activityRule.scenario,
            bundle
        )

        composeTestRule.onNodeWithTextResource(R.string.enter_matches_box_set_name)
            .assertIsDisplayed()
    }

    @Test
    fun idArg_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(15, "MBS1", 2)
        dataSource.addMatchesBoxSets(set)
        val bundle =
            MatchesBoxSetManipulatorFragmentArgs.Builder(bag.id, set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetManipulatorFragment>(
            composeTestRule.activityRule.scenario,
            bundle
        )

        composeTestRule.onNodeWithText(set.name).assertIsDisplayed()
    }

    @Test
    fun addNewSet_navigationCalled() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)

        val navController = Mockito.mock(NavController::class.java)

        val bundle =
            MatchesBoxSetManipulatorFragmentArgs.Builder(bag.id, ADD_NEW_ITEM_ID, "Title").build()
                .toBundle()
        launchFragment<MatchesBoxSetManipulatorFragment>(
            composeTestRule.activityRule.scenario,
            bundle
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New set")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        // Test navigation
        verify(navController).navigate(
            MatchesBoxSetManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(
                    bag.id,
                    bag.name
                )
        )
    }

    @Test
    fun updateSet_navigationCalled() = runBlocking {
        val bag = Bag(2, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val setUpdated = MatchesBoxSet(1, "Set updated", bag.id)
        dataSource.addMatchesBoxSets(set.copy())

        val navController = Mockito.mock(NavController::class.java)

        val bundle =
            MatchesBoxSetManipulatorFragmentArgs.Builder(bag.id, set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetManipulatorFragment>(
            composeTestRule.activityRule.scenario,
            bundle
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement(setUpdated.name)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        verify(navController).navigate(
            MatchesBoxSetManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxListFragment(
                    setUpdated.id,
                    setUpdated.name
                )
        )
    }

    @Test
    fun deleteSet_sizeNavigationEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "MBS", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)

        val navController = Mockito.mock(NavController::class.java)

        val bundle =
            MatchesBoxSetManipulatorFragmentArgs.Builder(DO_NOT_NEED_THIS_VARIABLE, set.id, "Title")
                .build().toBundle()
        launchFragment<MatchesBoxSetManipulatorFragment>(
            composeTestRule.activityRule.scenario,
            bundle
        ) {
            Navigation.setViewNavController(requireView(), navController)
            clickDeleteAction(this)
        }

        verify(navController).navigate(
            MatchesBoxSetManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(
                    bag.id,
                    bag.name
                )
        )
    }

    private fun clickDeleteAction(
        fragment: Fragment
    ) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        fragment.onOptionsItemSelected(deleteMenuItem)
    }
}