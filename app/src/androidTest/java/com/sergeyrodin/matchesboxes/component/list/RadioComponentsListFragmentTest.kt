package com.sergeyrodin.matchesboxes.component.list

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
import com.sergeyrodin.matchesboxes.DO_NOT_NEED_THIS_VARIABLE
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.*
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
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()
        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.no_components_added_text)).check(matches(isDisplayed()))
    }

    @Test
    fun fewItems_noItemsTextNotDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 1, box.id),
            RadioComponent(2, "Component2", 1, box.id),
            RadioComponent(3, "Component3", 1, box.id)
        )

        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.no_components_added_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun fewItems_textEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 1, box.id)
        val component2 = RadioComponent(2, "Component2", 1, box.id)
        val component3 = RadioComponent(3, "Component3", 1, box.id)
        dataSource.addRadioComponents(component1, component2, component3)

        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)

        onView(withText(component1.name)).check(matches(isDisplayed()))
        onView(withText(component2.name)).check(matches(isDisplayed()))
        onView(withText(component3.name)).check(matches(isDisplayed()))
    }

    @Test
    fun oneItem_quantityDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 12, box.id)
        dataSource.addRadioComponents(component)

        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)

        onView(withText(component.quantity.toString())).check(matches(isDisplayed()))
    }

    @Test
    fun addItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()
        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        val scenario = launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.add_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(ADD_NEW_ITEM_ID, bag, set, box)
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)

        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        val scenario = launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText(component.name)).perform(click())

        verify(navController).navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(component.id, bag, set, box)
        )
    }

    @Test
    fun boxEditClick_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()

        val bundle = RadioComponentsListFragmentArgs.Builder(bag, set, box).build().toBundle()
        val scenario = launchFragmentInContainer<RadioComponentsListFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickEditAction(scenario)

        verify(navController).navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteMatchesBoxFragment(bag, set, box)
        )
    }

    private fun clickEditAction(scenario: FragmentScenario<RadioComponentsListFragment>) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(editMenuItem)
        }
    }
}