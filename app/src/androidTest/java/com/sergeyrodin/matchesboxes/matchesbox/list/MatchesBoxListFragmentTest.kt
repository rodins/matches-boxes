package com.sergeyrodin.matchesboxes.matchesbox.list

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
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.DO_NOT_NEED_THIS_VARIABLE
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.matchesboxset.list.MatchesBoxSetsListFragment
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
@MediumTest
class MatchesBoxListFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun noData_noItemsTextDisplayed() {
        val setId = 1
        dataSource.addMatchesBoxes()
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_matches_boxes_added)).check(matches(isDisplayed()))
    }

    @Test
    fun oneItem_noItemsTextNotDisplayed() {
        val setId = 1
        dataSource.addMatchesBoxes(MatchesBox(1, "Box", setId))
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_matches_boxes_added)).check(matches(not(isDisplayed())))
    }

    @Test
    fun fewItems_namesEqual() {
        val setId = 1
        val setId2 = 2
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", setId),
            MatchesBox(2, "Box2", setId),
            MatchesBox(3, "Box3", setId),
            MatchesBox(4, "Box4", setId2)
        )
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)

        onView(withText("Box1")).check(matches(isDisplayed()))
        onView(withText("Box2")).check(matches(isDisplayed()))
        onView(withText("Box3")).check(matches(isDisplayed()))
        onView(withText("Box4")).check(doesNotExist())
    }

    @Test
    fun addItemClick_navigationCalled() {
        val setId = 1
        dataSource.addMatchesBoxes()
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        val scenario = launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.add_box_fab)).perform(click())

        verify(navController).navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxFragment(setId, ADD_NEW_ITEM_ID)
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val setId = 1
        val box = MatchesBox(1, "Box1", setId)
        dataSource.addMatchesBoxes(box)
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        val scenario = launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText(box.name)).perform(click())

        verify(navController).navigate(
            MatchesBoxListFragmentDirections.actionMatchesBoxListFragmentToRadioComponentsListFragment(box.id)
        )
    }

    @Test
    fun editAction_navigationCalled() {
        val setId = 1
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        val scenario = launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickEditAction(scenario)

        verify(navController).navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxSetFragment(setId, DO_NOT_NEED_THIS_VARIABLE)
        )
    }

    @Test
    fun boxInput_quantityDisplayed() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        val component1 = RadioComponent(1, "Component", 3, setId)
        val component2 = RadioComponent(2, "Component2", 7, setId)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2)
        val bundle = MatchesBoxListFragmentArgs.Builder(setId).build().toBundle()
        launchFragmentInContainer<MatchesBoxListFragment>(bundle, R.style.AppTheme)

        onView(withText("10")).check(matches(isDisplayed())) // sum of components quantities 3+7=10
    }

    private fun clickEditAction(scenario: FragmentScenario<MatchesBoxListFragment>) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(editMenuItem)
        }
    }

}