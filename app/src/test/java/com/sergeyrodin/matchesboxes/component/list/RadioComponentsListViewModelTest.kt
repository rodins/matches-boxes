package com.sergeyrodin.matchesboxes.component.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RadioComponentsListViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RadioComponentsListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        viewModel = RadioComponentsListViewModel(dataSource)
    }

    @Test
    fun noComponents_sizeZero() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        viewModel.startComponent(box.id)

        val items = viewModel.componentsList.getOrAwaitValue()

        assertThat(items.size, `is`(0))
    }

    @Test
    fun fewComponents_sizeEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 2, box.id),
            RadioComponent(2, "Component2", 2, box.id),
            RadioComponent(3, "Component3", 3, box.id)
        )
        viewModel.startComponent(box.id)

        val items = viewModel.componentsList.getOrAwaitValue()

        assertThat(items.size, `is`(3))
    }

    @Test
    fun noComponents_noItemsTextVisibleTrue() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        viewModel.startComponent(box.id)

        val visible = viewModel.noComponentsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun fewComponents_noItemsTextVisibleFalse() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 2, box.id),
            RadioComponent(2, "Component2", 2, box.id),
            RadioComponent(3, "Component3", 3, box.id)
        )
        viewModel.startComponent(box.id)

        val visible = viewModel.noComponentsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addComponent_eventNotNull() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        viewModel.startComponent(box.id)

        viewModel.addComponent()

        val value = viewModel.addComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(value, `is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
    fun quantityItems_namesEqual() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        val component1 =  RadioComponent(1, "Component1", 2, box.id)
        val component2 =  RadioComponent(2, "Component2", 2, box.id)
        val component3 =  RadioComponent(3, "Component3", 2, box.id)
        dataSource.addRadioComponents(component1, component2, component3)
        viewModel.startComponent(box.id)

        val items = viewModel.componentsList.getOrAwaitValue()
        assertThat(items[0].name, `is`(component1.name))
        assertThat(items[0].componentsQuantity, `is`(component1.quantity.toString()))

        assertThat(items[1].name, `is`(component2.name))
        assertThat(items[1].componentsQuantity, `is`(component2.quantity.toString()))

        assertThat(items[2].name, `is`(component3.name))
        assertThat(items[2].componentsQuantity, `is`(component3.quantity.toString()))
    }

    @Test
    fun selectComponent_selectedComponentEventIdEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        val component1 =  RadioComponent(1, "Component1", 2, box.id)
        val component2 =  RadioComponent(2, "Component2", 2, box.id)
        val component3 =  RadioComponent(3, "Component3", 2, box.id)
        dataSource.addRadioComponents(component1, component2, component3)
        viewModel.startComponent(box.id)

        viewModel.selectComponent(component2.id)

        val selectedId = viewModel.selectedComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(selectedId, `is`(component2.id))
    }
}