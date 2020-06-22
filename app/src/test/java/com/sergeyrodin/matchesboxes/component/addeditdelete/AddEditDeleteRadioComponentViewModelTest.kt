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
    fun noItem_quantityEmpty() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        val quantity = subject.quantity.getOrAwaitValue()

        assertThat(quantity, `is`(""))
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

        subject.name.value = "New component"
        subject.quantity.value = "3"

        subject.saveItem()

        val item = dataSource.getRadioComponentsByMatchesBoxId(boxId)[0]
        assertThat(item.name, `is`("New component"))
        assertThat(item.quantity, `is`(3))
        assertThat(item.matchesBoxId, `is`(boxId))
    }

    @Test
    fun addTwoItems_namesEqual() = runBlocking{
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.name.value = "Component1"
        subject.quantity.value = "3"

        subject.saveItem()

        val item = dataSource.getRadioComponentsByMatchesBoxId(boxId)[0]
        assertThat(item.name, `is`("Component1"))

        subject.name.value = "Component2"
        subject.quantity.value = "3"

        subject.saveItem()

        val item2 = dataSource.getRadioComponentsByMatchesBoxId(boxId)[1]
        assertThat(item2.name, `is`("Component2"))
    }

    @Test
    fun addItem_addEventNotNull() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.name.value = "New component"
        subject.quantity.value = "3"

        subject.saveItem()

        val unit = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(unit, `is`(not(nullValue())))
    }

    @Test
    fun updateItem_saveItem_nameEquals() = runBlocking{
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.name.value = "Updated component"
        subject.quantity.value = "4"

        subject.saveItem()

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

        subject.name.value = "Updated component"
        subject.quantity.value = "4"

        subject.saveItem()

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

    @Test
    fun plusButton_addQuantity_quantityEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.quantityPlus()

        val quantity = subject.quantity.getOrAwaitValue()
        assertThat(quantity, `is`((component.quantity + 1).toString()))
    }

    @Test
    fun newItem_plusButtonClick_quantityOne() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.quantityPlus()

        val quantity = subject.quantity.getOrAwaitValue()
        assertThat(quantity, `is`("1"))
    }

    @Test
    fun minusButton_subtractQuantity_quantityEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.quantityMinus()

        val quantity = subject.quantity.getOrAwaitValue()
        assertThat(quantity, `is`((component.quantity - 1).toString()))
    }

    @Test
    fun quantityZero_minusButtonEnabled() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 1, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        val minusDisabledFalse = subject.minusEnabled.getOrAwaitValue()
        assertThat(minusDisabledFalse, `is`(true))

        subject.quantityMinus()

        val minusDisabled = subject.minusEnabled.getOrAwaitValue()
        assertThat(minusDisabled, `is`(false))
    }

    @Test
    fun newItem_minusButtonEnabledFalse() {
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        val minusEnabled = subject.minusEnabled.getOrAwaitValue()
        assertThat(minusEnabled, `is`(false))
    }

    @Test
    fun quantityNegative_equalsQuantity() = runBlocking{
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.name.value = "Component updated"
        subject.quantity.value = "-1"

        subject.saveItem()

        val loaded = dataSource.getRadioComponentById(component.id)
        assertThat(loaded?.quantity, `is`(3))
    }

    @Test
    fun addItem_quantityEmpty_quantityZero() = runBlocking{
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.name.value = "Component"
        subject.quantity.value = ""

        subject.saveItem()

        val loaded = dataSource.getRadioComponentsByMatchesBoxId(boxId)[0]
        assertThat(loaded?.quantity, `is`(0))
    }

    @Test
    fun componentArg_isBuyIsFalse() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        val isBuy = subject.isBuy.getOrAwaitValue()
        assertThat(isBuy, `is`(false))
    }

    @Test
    fun componentArg_isBuyIsTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId, true)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        val isBuy = subject.isBuy.getOrAwaitValue()
        assertThat(isBuy, `is`(true))
    }

    @Test
    fun addComponent_isBuyTrue_isBuySaved() = runBlocking{
        val boxId = 1
        dataSource.addRadioComponents()
        subject.start(boxId, ADD_NEW_ITEM_ID)

        subject.name.value = "Component"
        subject.isBuy.value = true

        subject.saveItem()

        val loaded = dataSource.getRadioComponentsByMatchesBoxId(boxId)[0]
        assertThat(loaded.isBuy, `is`(true))
    }

    @Test
    fun updateComponent_isBuyTrue_isBuySaved() = runBlocking {
        val boxId = 1
        val component = RadioComponent(1, "Component", 18, boxId)
        dataSource.addRadioComponents(component)
        subject.start(boxId, component.id)

        subject.isBuy.value = true
        subject.saveItem()

        val loaded = dataSource.getRadioComponentById(component.id)
        assertThat(loaded?.isBuy, `is`(true))
    }
}