package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.bag.addeditdelete.AddEditDeleteBagFragment
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
                .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(ADD_NEW_ITEM_ID, bagId)
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val bagId = 2
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxSets(set)
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId).build().toBundle()
        val scenario = launchFragmentInContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText(set.name)).perform(click())

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToMatchesBoxListFragment(set.id)
        )
    }

    @Test
    fun clickEditAction_navigationMatches() {
        val bagId = 1
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId).build().toBundle()
        val scenario = launchFragmentInContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickEditAction(scenario)

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteBagFragment(bagId)
        )
    }

    private fun clickEditAction(scenario: FragmentScenario<MatchesBoxSetsListFragment>) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(editMenuItem)
        }
    }
}