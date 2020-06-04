package com.sergeyrodin.matchesboxes.component.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RadioComponentsListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: RadioComponentsListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = RadioComponentsListViewModel(dataSource)
    }

    @Test
    fun noItems_sizeZero() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId)

        val items = subject.items.getOrAwaitValue()

        assertThat(items.size, `is`(0))
    }

    @Test
    fun fewItems_sizeEquals() {
        val boxId = 1
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 2, boxId),
            RadioComponent(2, "Component2", 2, boxId),
            RadioComponent(3, "Component3", 3, boxId)
        )
        subject.start(boxId)

        val items = subject.items.getOrAwaitValue()

        assertThat(items.size, `is`(3))
    }

    @Test
    fun noItems_noItemsTextVisibleTrue() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId)

        val visible = subject.noItemsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun fewItems_noItemsTextVisibleFalse() {
        val boxId = 1
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 2, boxId),
            RadioComponent(2, "Component2", 2, boxId),
            RadioComponent(3, "Component3", 3, boxId)
        )
        subject.start(boxId)

        val visible = subject.noItemsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }
}