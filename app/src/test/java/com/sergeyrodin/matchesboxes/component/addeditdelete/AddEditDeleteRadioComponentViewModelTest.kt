package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
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

    @Test
    fun addItem_saveItem_nameEquals() = runBlocking{
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.saveItem("New component", "3")

        val item = dataSource.getRadioComponentsByMatchesBoxId(boxId)[0]
        assertThat(item.name, `is`("New component"))
        assertThat(item.quantity, `is`(3))
        assertThat(item.matchesBoxId, `is`(boxId))
    }

    @Test
    fun addItem_addEventNotNull() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.saveItem("New component", "3")

        val unit = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(unit, `is`(not(nullValue())))
    }

    @Test
    fun updateItem_saveItem_nameEquals() = runBlocking{
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.saveItem("Updated component", "4")

        val item = dataSource.getRadioComponentById(component.id)
        assertThat(item?.name, `is`("Updated component"))
        assertThat(item?.quantity, `is`(4))
        assertThat(item?.matchesBoxId, `is`(boxId))
    }

    @Test
    fun updateItem_saveItem_updateItemEventNotNull() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.saveItem("Updated component", "4")

        val event = subject.updateItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun deleteItem_sizeZero() = runBlocking{
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.deleteItem()

        val items = dataSource.getRadioComponentsByMatchesBoxId(boxId)
        assertThat(items.size, `is`(0))
    }

    @Test
    fun deleteItem_deleteEventNotNull() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.deleteItem()

        val event = subject.deleteItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }
}