package com.sergeyrodin.matchesboxes.component.list

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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
class RadioComponentsListFragmentTest {

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
    fun noItems_noItemsTextDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()
        launchFragment(box)

        composeTestRule.onNodeWithText(R.string.no_components_added).assertIsDisplayed()
    }

    @Test
    fun fewItems_noItemsTextNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 1, box.id),
            RadioComponent(2, "Component2", 1, box.id),
            RadioComponent(3, "Component3", 1, box.id)
        )

        launchFragment(box)

        composeTestRule.onNodeWithText(R.string.no_components_added).assertDoesNotExist()
    }

    @Test
    fun fewItems_textEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 1, box.id)
        val component2 = RadioComponent(2, "Component2", 1, box.id)
        val component3 = RadioComponent(3, "Component3", 1, box.id)
        dataSource.addRadioComponents(component1, component2, component3)

        launchFragment(box)

        composeTestRule.onNodeWithText(component1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(component2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(component3.name).assertIsDisplayed()
    }

    @Test
    fun oneItem_quantityDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 12, box.id)
        dataSource.addRadioComponents(component)

        launchFragment(box)

        val result = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            component.quantity,
            component.quantity.toString()
        )

        composeTestRule.onNodeWithText(result).assertIsDisplayed()
    }

    @Test
    fun addItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()

        val bundle = createBundle(box)
        val navController = Mockito.mock(NavController::class.java)
        var title = ""
        launchFragment<RadioComponentsListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(view!!, navController)
            title = getString(R.string.add_component)
        }

        composeTestRule.onNodeWithContentDescription(R.string.add_component).performClick()

        verify(navController).navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID,
                    box.id,
                    title,
                    "",
                    RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                )
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)
        val bundle = createBundle(box)

        launchFragment<RadioComponentsListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithText(component.name).performClick()

        verify(navController).navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToRadioComponentDetailsFragment(
                    component.id,
                    "",
                    RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                )
        )
    }

    @Test
    fun boxEditClick_navigationCalled() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = createBundle(box)
        launchFragment<RadioComponentsListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(view!!, navController)
            title = getString(R.string.update_box)
            clickEditAction(this)
        }

        verify(navController).navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteMatchesBoxFragment(
                    NO_ID_SET, box.id, title
                )
        )
    }

    private fun launchFragment(box: MatchesBox) {
        val bundle = createBundle(box)
        launchFragment(bundle)
    }

    private fun launchFragment(bundle: Bundle){
        launchFragment<RadioComponentsListFragment>(composeTestRule.activityRule.scenario, bundle)
    }

    private fun createBundle(box: MatchesBox): Bundle {
        return RadioComponentsListFragmentArgs.Builder(box.id, "Title").build().toBundle()
    }

    private fun clickEditAction(fragment: Fragment) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        fragment.onOptionsItemSelected(editMenuItem)
    }
}