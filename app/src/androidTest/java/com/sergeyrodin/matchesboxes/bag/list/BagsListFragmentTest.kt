package com.sergeyrodin.matchesboxes.bag.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.equalTo
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
class BagsListFragmentTest {

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
    fun addBag_bagDisplayed() {
        dataSource.addBags(
            Bag(1, "New bag")
        )
        dataSource.addRadioComponents()

        launchFragment<BagsListFragment>(composeTestRule.activityRule.scenario, null)

        composeTestRule.onNodeWithText("New bag").assertIsDisplayed()
    }

    @Test
    fun noBags_noBagsTextDisplayed() {
        dataSource.addBags()
        dataSource.addRadioComponents()

        launchFragment<BagsListFragment>(composeTestRule.activityRule.scenario, null)

        val emptyStateText = composeTestRule.activity.getString(R.string.no_bags_added)
        composeTestRule.onNodeWithText(emptyStateText).assertIsDisplayed()
    }

    @Test
    fun noBags_addButtonClicked() {
        dataSource.addBags()

        val navController = Mockito.mock(NavController::class.java)
        val title = composeTestRule.activity.getString(R.string.add_bag)

        launchFragment<BagsListFragment>(composeTestRule.activityRule.scenario, null) {
            Navigation.setViewNavController(view!!, navController)
        }

        composeTestRule.onNodeWithContentDescription(title).performClick()
        verify(navController).navigate(
            BagsListFragmentDirections.actionBagsListFragmentToAddEditDeleteBagFragment(
                ADD_NEW_ITEM_ID, title)
        )
    }

    @Test
    fun fewBags_itemClicked() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        dataSource.addBags(bag1, bag2)
        dataSource.addRadioComponents()

        val navController = Mockito.mock(NavController::class.java)

        launchFragment<BagsListFragment>(composeTestRule.activityRule.scenario, null) {
            Navigation.setViewNavController(view!!, navController)
        }

        composeTestRule.onNodeWithText("Bag2").performClick()
        verify(navController).navigate(
            BagsListFragmentDirections.actionBagsListFragmentToMatchesBoxSetsListFragment(bag2.id, bag2.name)
        )
    }

    @Test
    fun bagsList_quantityDisplayed() {
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
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        val component9 = RadioComponent(9, "Component9", 9, box5.id)
        val component10 = RadioComponent(10, "Component10", 10, box5.id)
        val component11 = RadioComponent(11, "Component11", 11, box6.id)
        val component12 = RadioComponent(12, "Component12", 12, box6.id)
        val component13 = RadioComponent(13, "Component13", 13, box7.id)
        val component14 = RadioComponent(14, "Component14", 14, box7.id)
        val component15 = RadioComponent(15, "Component15", 15, box8.id)
        val component16 = RadioComponent(16, "Component16", 16, box8.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        dataSource.addRadioComponents(component1, component2, component3, component4, component5,
            component6, component7, component8, component9, component10, component11, component12,
            component13, component14, component15, component16)
        launchFragment<BagsListFragment>(composeTestRule.activityRule.scenario, null)

        val quantity1 = "36"
        val quantity2 = "100"

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
    fun bagListIconIsDisplayed() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        launchFragment<BagsListFragment>(composeTestRule.activityRule.scenario, null)

        val bagIconDescription = composeTestRule.activity.getString(R.string.bag_icon_description)
        composeTestRule.onNodeWithContentDescription(bagIconDescription).assertIsDisplayed()
    }
}