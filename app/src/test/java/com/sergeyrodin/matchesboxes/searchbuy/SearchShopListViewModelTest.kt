package com.sergeyrodin.matchesboxes.searchbuy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.MainCoroutineRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import com.sergeyrodin.matchesboxes.searchbuy.list.SearchShopListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchShopListViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: SearchShopListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject =
            SearchShopListViewModel(
                dataSource
            )
    }

    @Test
    fun queryInput_nameMatches() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("compo", true)

        val items = subject.items.getOrAwaitValue()
        assertThat(items[0].name, `is`(component.name))
    }

    @Test
    fun queryInput_nothingFound_sizeZero() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("box", true)

        val items = subject.items.getOrAwaitValue()
        assertThat(items.size, `is`(0))
    }

    @Test
    fun noComponentsTextVisible_nothingFound_equalsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("box", true)

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun emptyQuery_nothingFound_equalsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("", true)

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun noComponentsTextVisible_resultsFound_equalsFalse() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("compo", true)

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(false))
    }

    // Buy

    @Test
    fun buyMode_nameEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId, true)
        dataSource.addRadioComponents(component)
        subject.start("", false)

        val items = subject.items.getOrAwaitValue()
        assertThat(items[0].name, `is`(component.name))
    }

    @Test
    fun buyMode_noComponents_equalsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("", false)

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun addComponent_eventNotNull() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()
        subject.start("", false)

        subject.addComponent()

        val event = subject.addComponentEvent.getOrAwaitValue()
        assertThat(event, `is`(not(nullValue())))
    }
}