package com.sergeyrodin.matchesboxes.popular

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class PopularComponentsFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun noComponents_noComponentsTextDisplayed() {
        dataSource.addRadioComponents()
        dataSource.addHistory()
        launchFragmentInHiltContainer<PopularComponentsFragment>(null, R.style.AppTheme)

        onView(withText(R.string.no_popular_components)).check(matches(isDisplayed()))
    }

    @Test
    fun oneComponent_noComponentsTextIsNotDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        launchFragmentInHiltContainer<PopularComponentsFragment>(null, R.style.AppTheme)

        onView(withText(R.string.no_popular_components)).check(matches(not(isDisplayed())))
    }

    @Test
    fun oneComponent_nameDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        launchFragmentInHiltContainer<PopularComponentsFragment>(null, R.style.AppTheme)

        onView(withText(component.name)).check(matches(isDisplayed()))
    }

    @Test
    fun oneComponent_placeDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        launchFragmentInHiltContainer<PopularComponentsFragment>(null, R.style.AppTheme)

        onView(withText("1")).check(matches(isDisplayed()))
    }

    @Test
    fun threeComponents_placesDisplayed() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId)
        val component3 = RadioComponent(3, "Component3", 3, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component3.id, component3.quantity)
        dataSource.addRadioComponents(component1, component2, component3)
        dataSource.addHistory(history1, history2, history3)

        launchFragmentInHiltContainer<PopularComponentsFragment>(null, R.style.AppTheme)

        onView(withText("1")).check(matches(isDisplayed()))
        onView(withText("2")).check(matches(isDisplayed()))
        onView(withText("3")).check(matches(isDisplayed()))
    }
}