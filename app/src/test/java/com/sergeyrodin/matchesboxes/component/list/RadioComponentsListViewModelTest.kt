package com.sergeyrodin.matchesboxes.component.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RadioComponentsListViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: RadioComponentsListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = RadioComponentsListViewModel(dataSource)
    }

    @Test
    fun noComponents_sizeZero() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        subject.startComponent(box.id)

        val items = subject.componentsList.getOrAwaitValue()

        assertThat(items.size, CoreMatchers.`is`(0))
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
        subject.startComponent(box.id)

        val items = subject.componentsList.getOrAwaitValue()

        assertThat(items.size, CoreMatchers.`is`(3))
    }

    @Test
    fun noComponents_noItemsTextVisibleTrue() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        subject.startComponent(box.id)

        val visible = subject.noComponentsTextVisible.getOrAwaitValue()

        assertThat(visible, CoreMatchers.`is`(true))
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
        subject.startComponent(box.id)

        val visible = subject.noComponentsTextVisible.getOrAwaitValue()

        assertThat(visible, CoreMatchers.`is`(false))
    }

    @Test
    fun addComponent_eventNotNull() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        subject.startComponent(box.id)

        subject.addComponent()

        val value = subject.addComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(value, CoreMatchers.`is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }
}