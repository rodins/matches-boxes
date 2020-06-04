package com.sergeyrodin.matchesboxes.component.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
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
@MediumTest
class RadioComponentsListFragmentTest {
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
    fun noItems_noItemsTextDisplayed() {
        val boxId = 1
        dataSource.addRadioComponents()
        val bundle = RadioComponentsListFragmentArgs.Builder(boxId).build().toBundle()
        launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.no_components_added_text)).check(matches(isDisplayed()))
    }

    @Test
    fun fewItems_noItemsTextNotDisplayed() {
        val boxId = 1
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 1, boxId),
            RadioComponent(2, "Component2", 1, boxId),
            RadioComponent(3, "Component3", 1, boxId)
        )

        val bundle = RadioComponentsListFragmentArgs.Builder(boxId).build().toBundle()
        launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.no_components_added_text)).check(matches(not(isDisplayed())))
    }
}