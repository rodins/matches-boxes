package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.HiltTestActivity
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.launchFragment
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

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class MatchesBoxSetsListFragmentTest {

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
    fun addSets_namesDisplayed() {
        val bag = Bag(1, "Bag")
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bag.id),
            MatchesBoxSet(2, "MBS2", bag.id),
            MatchesBoxSet(3, "MBS3", bag.id)
        )

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithText("MBS1").assertIsDisplayed()
        composeTestRule.onNodeWithText("MBS2").assertIsDisplayed()
        composeTestRule.onNodeWithText("MBS3").assertIsDisplayed()
    }

    @Test
    fun noSets_textDisplayed() {
        val bag = Bag(1, "Bag")
        dataSource.addMatchesBoxSets()
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle)

        val setsEmptyText = composeTestRule.activity.getString(R.string.no_matches_box_sets_added)
        composeTestRule.onNodeWithText(setsEmptyText).assertIsDisplayed()
    }

    @Test
    fun noSets_addSetFabClicked_navigationCalled() {
        val bag = Bag(1, "Bag")

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.add_set)
        }

        val addSetDescription = composeTestRule.activity.getString(R.string.add_set)
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(
                    bag.id,
                    ADD_NEW_ITEM_ID,
                    title
                )
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val bag = Bag(2, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxSets(set)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithText(set.name).performClick()

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToMatchesBoxListFragment(set.id, set.name)
        )
    }

    @Test
    fun clickEditAction_navigationMatches() {
        val bag = Bag(1, "Bag")

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.update_bag)
            clickEditAction(this)
        }

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteBagFragment(bag.id, title)
        )
    }

    private fun clickEditAction(fragment: Fragment) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        fragment.onOptionsItemSelected(editMenuItem)
    }

    @Test
    fun setsList_quantityDisplayed() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(
            component1, component2, component3, component4,
            component5, component6, component7, component8
        )
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle)

        val quantity1 = "10"
        val quantity2 = "26"

        val result1 = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            quantity1.toInt(),
            quantity1
        )

        val result2 = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            quantity2.toInt(),
            quantity2
        )

        composeTestRule.onNodeWithText(result1).assertIsDisplayed()
        composeTestRule.onNodeWithText(result2).assertIsDisplayed()
    }

    @Test
    fun setListIconIsDisplayed() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxSets(set)
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId, "Title").build().toBundle()
        launchFragment<MatchesBoxSetsListFragment>(composeTestRule.activityRule.scenario, bundle)

        val setIconDescription = composeTestRule.activity.getString(R.string.set_icon_description)
        composeTestRule.onNodeWithContentDescription(setIconDescription).assertIsDisplayed()
    }
}