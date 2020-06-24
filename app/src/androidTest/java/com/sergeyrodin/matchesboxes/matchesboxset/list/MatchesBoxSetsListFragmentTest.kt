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
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponent
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

    @Test
    fun setsList_quantityDisplayed() {
        val bagId = 1
        val set1 = MatchesBoxSet(1, "Set1", bagId)
        val set2 = MatchesBoxSet(2, "Set2", bagId)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component1, component2, component3, component4,
            component5, component6, component7, component8)
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId).build().toBundle()
        launchFragmentInContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withText("10")).check(matches(isDisplayed()))
        onView(withText("26")).check(matches(isDisplayed()))
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