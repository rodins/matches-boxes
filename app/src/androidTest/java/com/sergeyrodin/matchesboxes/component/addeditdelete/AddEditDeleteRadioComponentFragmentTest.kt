package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.matchesbox.addeditdelete.AddEditDeleteMatchesBoxFragment
import com.sergeyrodin.matchesboxes.matchesbox.addeditdelete.AddEditDeleteMatchesBoxFragmentArgs
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditDeleteRadioComponentFragmentTest {
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
    fun noItem_hintDisplayed() {
        val boxId = 1
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, ADD_NEW_ITEM_ID).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.component_edit)).check(matches(withHint(R.string.enter_component_name)))
    }

    @Test
    fun argItem_nameEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.component_edit)).check(matches(withText(component.name)))
    }

}