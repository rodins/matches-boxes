package com.sergeyrodin.matchesboxes.searchbuy.edit

import android.app.Service
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.*
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class SearchBuyEditFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun clearDataSouce() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun componentId_fieldsEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val bundle = SearchBuyEditFragmentArgs.Builder(component.id, "", true).build().toBundle()
        launchFragmentInContainer<SearchBuyEditFragment>(bundle, R.style.AppTheme)

        onView(withText(bag.name)).check(matches(isDisplayed()))
        onView(withText(set.name)).check(matches(isDisplayed()))
        onView(withText(box.name)).check(matches(isDisplayed()))
        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @Test
    fun componentId_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val bundle = SearchBuyEditFragmentArgs.Builder(component.id, "", true).build().toBundle()
        launchFragmentInContainer<SearchBuyEditFragment>(bundle, R.style.AppTheme)

        onView(withText(component.quantity.toString())).check(matches(isDisplayed()))
    }

    @Test
    fun quantityPlusClick_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val bundle = SearchBuyEditFragmentArgs.Builder(component.id, "", true).build().toBundle()
        launchFragmentInContainer<SearchBuyEditFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.button_plus)).perform(click())

        onView(withText((component.quantity + 1).toString())).check(matches(isDisplayed()))
    }

    @Test
    fun quantityMinusClick_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val bundle = SearchBuyEditFragmentArgs.Builder(component.id, "", true).build().toBundle()
        launchFragmentInContainer<SearchBuyEditFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.button_minus)).perform(click())

        onView(withText((component.quantity - 1).toString())).check(matches(isDisplayed()))
    }

    @Test
    fun quantityZero_minusDisabled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 0,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val bundle = SearchBuyEditFragmentArgs.Builder(component.id, "", true).build().toBundle()
        launchFragmentInContainer<SearchBuyEditFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.button_minus)).check(matches(not(isEnabled())))
    }

    @Test
    fun saveItem_navigationCalled() {
        val query = ""
        val isSearch = true
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        val bundle = SearchBuyEditFragmentArgs.Builder(component.id, query, isSearch).build().toBundle()
        val scenario = launchFragmentInContainer<SearchBuyEditFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            SearchBuyEditFragmentDirections.actionSearchBuyEditFragmentToSearchBuyFragment(query, isSearch)
        )
    }


}