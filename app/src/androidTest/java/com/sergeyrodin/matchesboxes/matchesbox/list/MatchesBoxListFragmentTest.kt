package com.sergeyrodin.matchesboxes.matchesbox.list

import android.content.Context
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
class MatchesBoxListFragmentTest {

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
    fun noData_noItemsTextDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxes()
        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTextResource(R.string.no_matches_boxes_added).assertIsDisplayed()
    }

    @Test
    fun oneItem_noItemsTextNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxes(MatchesBox(1, "Box", set.id))
        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTextResource(R.string.no_matches_boxes_added).assertDoesNotExist()
    }

    @Test
    fun fewItems_namesEqual() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", set.id),
            MatchesBox(2, "Box2", set.id),
            MatchesBox(3, "Box3", set.id),
            MatchesBox(4, "Box4", set2.id)
        )
        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithText("Box1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Box2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Box3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Box4").assertDoesNotExist()
    }

    @Test
    fun addItemClick_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxes()

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.add_box)
        }

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()

        verify(navController).navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxFragment(set.id, ADD_NEW_ITEM_ID, title)
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box1", set.id)
        dataSource.addMatchesBoxes(box)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithText(box.name).performClick()

        verify(navController).navigate(
            MatchesBoxListFragmentDirections.actionMatchesBoxListFragmentToRadioComponentsListFragment(box.id, box.name)
        )
    }

    @Test
    fun editAction_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.update_set)
            clickEditAction(this)
        }

        verify(navController).navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxSetFragment(
                    DO_NOT_NEED_THIS_VARIABLE, set.id, title)
        )
    }

    private fun clickEditAction(fragment: Fragment) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        fragment.onOptionsItemSelected(editMenuItem)
    }

    @Test
    fun boxInput_quantityDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component", 3, set.id)
        val component2 = RadioComponent(2, "Component2", 7, set.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2)
        val bundle = MatchesBoxListFragmentArgs.Builder(set.id, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle)

        val quantity = "10"

        val result = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            quantity.toInt(),
            quantity
        )

        composeTestRule.onNodeWithText(result).assertIsDisplayed() // sum of components quantities 3+7=10
    }

    @Test
    fun boxesListIconIsDisplayed() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        val bundle = MatchesBoxListFragmentArgs.Builder(setId, "Title").build().toBundle()
        launchFragment<MatchesBoxListFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithContentDescriptionResource(R.string.box_icon_description).assertIsDisplayed()
    }
}