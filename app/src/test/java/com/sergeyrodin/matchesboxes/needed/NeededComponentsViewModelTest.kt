package com.sergeyrodin.matchesboxes.needed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NeededComponentsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: NeededComponentsViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        viewModel = NeededComponentsViewModel(dataSource)
    }

    @Test
    fun neededComponent_nameEquals() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 4, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId, true)
        dataSource.addRadioComponents(component1, component2)

        val items = viewModel.items.getOrAwaitValue()
        assertThat(items[0].name, `is`(component2.name))
        assertThat(items[0].componentsQuantity, `is`(component2.quantity.toString()))
    }

    @Test
    fun noNeededComponents_noComponentsTextEqualsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)

        val isVisible = viewModel.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun addComponent_eventNotNull() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()

        viewModel.addComponent()

        val event = viewModel.addComponentEvent.getOrAwaitValue()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectComponent_selectComponentEventIdEquals() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 4, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId, true)
        dataSource.addRadioComponents(component1, component2)

        viewModel.selectComponent(component2.id)

        val selectedId = viewModel.selectedComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(selectedId, `is`(component2.id))
    }
}