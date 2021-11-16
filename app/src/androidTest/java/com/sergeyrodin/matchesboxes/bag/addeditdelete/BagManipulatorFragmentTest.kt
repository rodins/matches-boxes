package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
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

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class BagManipulatorFragmentTest {

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
    fun nulArg_enterNameTextEquals() {
        val bundle = BagManipulatorFragmentArgs.Builder(ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTextResource(R.string.enter_bag_name).assertIsDisplayed()
    }

    @Test
    fun bagId_textEquals() {
        val bag = Bag(1, "New bag")
        dataSource.addBags(bag)
        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithText(bag.name).assertIsDisplayed()
    }

    @Test
    fun nullArg_addNewBag() {
        dataSource.addBags()

        val navController = Mockito.mock(NavController::class.java)

        val bundle = BagManipulatorFragmentArgs.Builder(ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(view!!, navController)
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput("New bag")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_bag).performClick()

        val bag = runBlocking { dataSource.getBags()[0] }
        assertThat(bag.name, `is`("New bag"))

        verify(navController).navigate(
            BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
        )
    }

    @Test
    fun bagIdArg_updateBagName() {
        val bag = Bag(1, "Old bag")
        val bagUpdated = Bag(1, "Bag updated")
        dataSource.addBags(bag.copy())

        val navController = Mockito.mock(NavController::class.java)

        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(view!!, navController)
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement(bagUpdated.name)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_bag).performClick()

        verify(navController).navigate(
            BagManipulatorFragmentDirections
                .actionAddEditDeleteBagFragmentToMatchesBoxSetsListFragment(bagUpdated.id, bagUpdated.name)
        )
    }

    @Test
    fun nullArg_emptyInput_sizeZero() {
        dataSource.addBags()
        val bundle = BagManipulatorFragmentArgs.Builder(ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput(" ")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_bag).performClick()

        val bags = runBlocking { dataSource.getBags() }
        assertThat(bags.size, `is`(0))
    }

    @Test
    fun bagIdArg_emptyInput_nameNotChanged() {
        val bag = Bag(1, "Old bag")
        dataSource.addBags(bag)

        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement(" ")
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_bag).performClick()

        val updatedBag = runBlocking { dataSource.getBags()[0] }
        assertThat(updatedBag.name, `is`("Old bag"))
    }

    @Test
    fun deleteBagAndNavigate() {
        val bag = Bag(1, "Bag to delete")
        dataSource.addBags(bag)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<BagManipulatorFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(view!!, navController)
            clickDeleteAction(this)
        }

        val bags = runBlocking { dataSource.getBags() }
        assertThat(bags.size, `is`(0))
        verify(navController).navigate(
            BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
        )
    }

    private fun clickDeleteAction(fragment: Fragment) {
        // Create dummy menu item with the desired item id
        val context = getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        fragment.onOptionsItemSelected(deleteMenuItem)
    }

}