package com.sergeyrodin.matchesboxes.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.data.*
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun clearDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun nothingFound_textEquals() {
        dataSource.addRadioComponents()
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragmentInContainer<SearchFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_components_found)).check(matches(isDisplayed()))
    }

    @Test
    fun componentsFound_textNotEquals() {
        val bagId = 1
        val component = RadioComponent(1, "Component", 3, bagId)
        dataSource.addRadioComponents(component)
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        launchFragmentInContainer<SearchFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_components_found)).check(matches(CoreMatchers.not(isDisplayed())))
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
        launchFragmentInContainer<SearchFragment>(bundle, R.style.AppTheme)

        onView(withText(component1.name)).check(matches(isDisplayed()))
        onView(withText(component2.name)).check(matches(isDisplayed()))
        onView(withText(component3.name)).check(matches(isDisplayed()))
    }

    @Test
    fun selectComponent_navigationCalled() {
        val bagId = 1
        val component1 = RadioComponent(1, "Component1", 2, bagId)
        val component2 = RadioComponent(2, "Component2", 3, bagId)
        val component3 = RadioComponent(3, "Component3", 3, bagId)
        dataSource.addRadioComponents(component1, component2, component3)
        val query = "compo"
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        val scenario = launchFragmentInContainer<SearchFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText(component2.name)).perform(ViewActions.click())
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
        launchFragmentInContainer<SearchFragment>(bundle, R.style.AppTheme)

        onView(withText(component.quantity.toString())).check(matches(isDisplayed()))
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
        val bundle = SearchFragmentArgs.Builder().setQuery(query).build().toBundle()
        val scenario = launchFragmentInContainer<SearchFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        var title = ""
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
            title = it.getString(R.string.add_component)
        }

        onView(ViewMatchers.withId(R.id.add_search_buy_component_fab)).perform(ViewActions.click())

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