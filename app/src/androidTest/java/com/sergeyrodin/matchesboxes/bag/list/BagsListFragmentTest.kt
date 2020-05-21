package com.sergeyrodin.matchesboxes.bag.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class BagsListFragmentTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
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
    fun addBag_BagDisplayed() {
        dataSource.addBags(
            Bag(1, "New bag")
        )

        launchFragmentInContainer<BagsListFragment>(null, R.style.AppTheme)

        onView(withText("New bag")).check(matches(isDisplayed()))
    }

    @Test
    fun noBags_noBagsTextDisplayed() {
        dataSource.addBags()

        launchFragmentInContainer<BagsListFragment>(null, R.style.AppTheme)

        onView(withText(R.string.no_bags_added)).check(matches(isDisplayed()))
    }

    @Test
    fun noBags_addButtonClicked() {
        // TODO: write this test
    }

    @Test
    fun fewBags_itemClicked() {
        // TODO: write this test
    }
}