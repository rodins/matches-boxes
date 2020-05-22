package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
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
class AddEditDeleteBagFragmentTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun resetDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun nullArg_enterNameTextEquals() {
        val bundle = AddEditDeleteBagFragmentArgs.Builder().setId(-1).build().toBundle()
        launchFragmentInContainer<AddEditDeleteBagFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.enter_bag_name)).check(matches(isDisplayed()))
    }

    @Test
    fun bagId_textEquals() {
        val bag = Bag(1, "New bag")
        dataSource.addBags(bag)
        val bundle = AddEditDeleteBagFragmentArgs.Builder().setId(bag.id).build().toBundle()
        launchFragmentInContainer<AddEditDeleteBagFragment>(bundle, R.style.AppTheme)

        onView(withText(bag.name)).check(matches(isDisplayed()))
    }

}