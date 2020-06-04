package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditDeleteRadioComponentViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: AddEditDeleteRadioComponentViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = AddEditDeleteRadioComponentViewModel(dataSource)
    }

    @Test
    fun noItem_nameEmpty() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(""))
    }

    @Test
    fun argItem_nameEquals() {
        val boxId = 1
        val component = RadioComponent(2, "Component", 2, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(component.name))
    }

    @Test
    fun noItem_quantityZero() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        val quantity = subject.quantity.getOrAwaitValue()

        assertThat(quantity, `is`("0"))
    }

    @Test
    fun argItem_quantityEquals() {
        val boxId = 1
        val component = RadioComponent(2, "Component", 2, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        val quantity = subject.quantity.getOrAwaitValue()

        assertThat(quantity, `is`(component.quantity.toString()))
    }
}