package com.sergeyrodin.matchesboxes.component.details

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.data.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@MediumTest
@RunWith(AndroidJUnit4::class)
class RadioComponentDetailsFragmentTest {
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun reset() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun componentId_detailsDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id, true)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val bundle = RadioComponentDetailsFragmentArgs.Builder(
            component.id,
            "",
            RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
        ).build().toBundle()
        launchFragmentInContainer<RadioComponentDetailsFragment>(bundle, R.style.AppTheme)

        onView(withText(bag.name)).check(matches(isDisplayed()))
        onView(withText(set.name)).check(matches(isDisplayed()))
        onView(withText(box.name)).check(matches(isDisplayed()))
        onView(withText(component.name)).check(matches(isDisplayed()))
        onView(withText(component.quantity.toString())).check(matches(isDisplayed()))
        onView(withText(R.string.buy_component)).check(matches(isChecked()))
    }

    @Test
    fun editItemClick_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id, true)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val bundle = RadioComponentDetailsFragmentArgs.Builder(
            component.id,
            "",
            RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
        ).build().toBundle()
        val scenario =
            launchFragmentInContainer<RadioComponentDetailsFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        var title = ""
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
            title = it.getString(R.string.update_component)
        }

        onView(withId(R.id.edit_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentDetailsFragmentDirections
                .actionRadioComponentDetailsFragmentToAddEditDeleteRadioComponentFragment(
                    component.id,
                    component.matchesBoxId,
                    title,
                    "",
                    RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                )
        )
    }
}