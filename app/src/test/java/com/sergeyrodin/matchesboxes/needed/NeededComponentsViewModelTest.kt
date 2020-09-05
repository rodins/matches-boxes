package com.sergeyrodin.matchesboxes.needed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NeededComponentsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: NeededComponentsViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = NeededComponentsViewModel(dataSource)
    }

    @Test
    fun neededComponent_nameEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId, true)
        dataSource.addRadioComponents(component)

        val items = subject.items.getOrAwaitValue()
        assertThat(items[0].name, `is`(component.name))
    }

    @Test
    fun noNeededComponents_noComponentsTextEqualsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)

        val isVisible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(isVisible, `is`(true))
    }

    @Test
    fun addComponent_eventNotNull() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()

        subject.addComponent()

        val event = subject.addComponentEvent.getOrAwaitValue()
        assertThat(event, `is`(not(nullValue())))
    }
}