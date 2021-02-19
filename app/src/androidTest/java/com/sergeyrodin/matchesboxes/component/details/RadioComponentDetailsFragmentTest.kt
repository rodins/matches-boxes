package com.sergeyrodin.matchesboxes.component.details

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class RadioComponentDetailsFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
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
        launchFragmentInHiltContainer<RadioComponentDetailsFragment>(bundle, R.style.AppTheme)

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

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        launchFragmentInHiltContainer<RadioComponentDetailsFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(view!!, navController)
            title = getString(R.string.update_component)
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