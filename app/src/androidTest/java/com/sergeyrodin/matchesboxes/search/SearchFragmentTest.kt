package com.sergeyrodin.matchesboxes.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
class SearchFragmentTest {

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
    fun nothingFound_textEquals() {
        dataSource.addRadioComponents()
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragment<SearchFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTextResource(R.string.no_components_found).assertIsDisplayed()
    }

    @Test
    fun componentsFound_textNotEquals() {
        val bagId = 1
        val component = RadioComponent(1, "Component", 3, bagId)
        dataSource.addRadioComponents(component)
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragment<SearchFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithTextResource(R.string.no_components_found).assertDoesNotExist()
    }

    @Test
    fun componentsFound_componentsEqual() {
        val bagId = 1
        val component1 = RadioComponent(1, "Component1", 2, bagId)
        val component2 = RadioComponent(2, "Component2", 3, bagId)
        val component3 = RadioComponent(3, "Component3", 3, bagId)
        dataSource.addRadioComponents(component1, component2, component3)
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragment<SearchFragment>(composeTestRule.activityRule.scenario, bundle)

        composeTestRule.onNodeWithText(component1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(component2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(component3.name).assertIsDisplayed()
    }

    @Test
    fun selectComponent_navigationCalled() {
        val bagId = 1
        val component1 = RadioComponent(1, "Component1", 2, bagId)
        val component2 = RadioComponent(2, "Component2", 3, bagId)
        val component3 = RadioComponent(3, "Component3", 3, bagId)
        dataSource.addRadioComponents(component1, component2, component3)
        val query = "compo"

        val navController = Mockito.mock(NavController::class.java)

        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragment<SearchFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
        }

        composeTestRule.onNodeWithText(component2.name).performClick()

        verify(navController).navigate(
            SearchFragmentDirections.actionSearchFragmentToRadioComponentDetailsFragment(
                component2.id,
                query,
                RadioComponentManipulatorReturns.TO_SEARCH_LIST
            )
        )
    }

    @Test
    fun componentFound_quantityDisplayed() {
        val bagId = 1
        val component = RadioComponent(1, "Component", 12, bagId)
        dataSource.addRadioComponents(component)
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragment<SearchFragment>(composeTestRule.activityRule.scenario, bundle)

        val result = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            component.quantity,
            component.quantity.toString()
        )

        composeTestRule.onNodeWithText(result).assertIsDisplayed()
    }

    @Test
    fun componentFound_addNewComponent_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val query = "compo"

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragment<SearchFragment>(composeTestRule.activityRule.scenario, bundle) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.add_component)
        }

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()

        verify(navController).navigate(
            SearchFragmentDirections.actionSearchFragmentToAddEditDeleteRadioComponentFragment(
                ADD_NEW_ITEM_ID,
                NO_ID_SET,
                title,
                query,
                RadioComponentManipulatorReturns.TO_SEARCH_LIST
            )
        )
    }
}