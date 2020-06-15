package com.sergeyrodin.matchesboxes.searchbuy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBuyFragmentTest{
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
        val isSearch = true
        val bundle = SearchBuyFragmentArgs.Builder(query, isSearch).build().toBundle()
        launchFragmentInContainer<SearchBuyFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_components_found)).check(matches(isDisplayed()))
    }

    @Test
    fun componentsFound_textNotEquals() {
        val bagId = 1
        val component = RadioComponent(1, "Component", 3, bagId)
        dataSource.addRadioComponents(component)
        val query = "compo"
        val isSearch = true
        val bundle = SearchBuyFragmentArgs.Builder(query, isSearch).build().toBundle()
        launchFragmentInContainer<SearchBuyFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_components_found)).check(matches(not(isDisplayed())))
    }

    @Test
    fun componentsFound_componentsEqual() {
        val bagId = 1
        val component1 = RadioComponent(1, "Component1", 2, bagId)
        val component2 = RadioComponent(2, "Component2", 3, bagId)
        val component3 = RadioComponent(3, "Component3", 3, bagId)
        dataSource.addRadioComponents(component1, component2, component3)

        val query = "compo"
        val isSearch = true
        val bundle = SearchBuyFragmentArgs.Builder(query, isSearch).build().toBundle()
        launchFragmentInContainer<SearchBuyFragment>(bundle, R.style.AppTheme)

        onView(withText(component1.name)).check(matches(isDisplayed()))
        onView(withText(component2.name)).check(matches(isDisplayed()))
        onView(withText(component3.name)).check(matches(isDisplayed()))
    }

}