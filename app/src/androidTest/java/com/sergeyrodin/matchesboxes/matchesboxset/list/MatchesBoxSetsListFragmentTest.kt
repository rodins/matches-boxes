package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@MediumTest
@RunWith(AndroidJUnit4::class)
class MatchesBoxSetsListFragmentTest {
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
    fun addSets_namesDisplayed() {
        val bagId = 1
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bagId),
            MatchesBoxSet(2, "MBS2", bagId),
            MatchesBoxSet(3, "MBS3", bagId)
        )

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId).build().toBundle()
        launchFragmentInContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withText("MBS1")).check(matches(isDisplayed()))
        onView(withText("MBS2")).check(matches(isDisplayed()))
        onView(withText("MBS3")).check(matches(isDisplayed()))
    }

    @Test
    fun noSets_textDisplayed() {
        val bagId = 1
        dataSource.addMatchesBoxSets()
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId).build().toBundle()
        launchFragmentInContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))
    }

    @Test
    fun noSets_addSetFabClicked_navigationCalled() {
        val bagId = 1
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId).build().toBundle()
        val scenario = launchFragmentInContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.add_set_fab)).perform(click())

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(bagId, ADD_NEW_ITEM_ID)
        )
    }
}