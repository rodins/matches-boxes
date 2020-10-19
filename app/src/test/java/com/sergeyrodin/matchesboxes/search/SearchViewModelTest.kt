package com.sergeyrodin.matchesboxes.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: SearchViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = SearchViewModel(dataSource)
    }

    @Test
    fun queryInput_nameMatches() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("compo")

        val items = subject.items.getOrAwaitValue()
        assertThat(items[0].name, `is`(component.name))
    }

    @Test
    fun queryInput_nothingFound_sizeZero() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("box")

        val items = subject.items.getOrAwaitValue()
        assertThat(items.size, `is`(0))
    }

    @Test
    fun noComponentsTextVisible_nothingFound_equalsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("box")

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun emptyQuery_nothingFound_equalsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("")

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun noComponentsTextVisible_resultsFound_equalsFalse() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start("compo")

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(false))
    }

    @Test
    fun twoSearches_nameEquals() {
        val boxId = 1
        val component1 = RadioComponent(1, "LA78040", 3, boxId)
        val component2 = RadioComponent(2, "BUH1015", 3, boxId)
        dataSource.addRadioComponents(component1, component2)

        subject.start("78")
        val items1 = subject.items.getOrAwaitValue()
        assertThat(items1[0].name, `is`(component1.name))

        subject.start("15")
        val items2 = subject.items.getOrAwaitValue()
        assertThat(items2[0].name, `is`(component2.name))
    }
}