package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.data.*
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
        assertThat(loaded.quantity, `is`(0))
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

    @Test
    fun addComponent_boxTitleEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents()
        subject.start(box.id, ADD_NEW_ITEM_ID)

        subject.name.value = "New component"
        subject.quantity.value = "3"

        subject.saveItem()

        val title = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(title, `is`(box.name))
    }

    @Test
    fun updateComponent_boxTitleEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        val component = RadioComponent(1, "Component", 4, box.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(box.id, component.id)

        subject.name.value = "Updated component"

        subject.saveItem()

        val title = subject.updateItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(title, `is`(box.name))
    }

    @Test
    fun deleteComponent_boxTitleEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        val component = RadioComponent(1, "Component", 4, box.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(box.id, component.id)

        subject.deleteItem()

        val title = subject.deleteItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(title, `is`(box.name))
    }

    // Spinners

    @Test
    fun boxId_boxNamesEquals() {
        val bag = Bag(3, "Bag")
        val set = MatchesBoxSet(5, "Set", bag.id)
        val box1 = MatchesBox(6, "Box1", set.id)
        val box2 = MatchesBox(7, "Box2", set.id)
        val component = RadioComponent(1, "Component", 3, box1.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box1, box2)
        dataSource.addRadioComponents(component)

        subject.start(box1.id, component.id)

        val boxNames = subject.boxNames.getOrAwaitValue()
        assertThat(boxNames[0], `is`(box1.name))
        assertThat(boxNames[1], `is`(box2.name))
    }

    @Test
    fun boxId_setNamesEquals() {
        val bag = Bag(3, "Bag")
        val set1 = MatchesBoxSet(5, "Set1", bag.id)
        val set2 = MatchesBoxSet(6, "Set2", bag.id)
        val box = MatchesBox(6, "Box1", set1.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        subject.start(box.id, component.id)

        val setNames = subject.setNames.getOrAwaitValue()
        assertThat(setNames[0], `is`(set1.name))
        assertThat(setNames[1], `is`(set2.name))
    }

    @Test
    fun getBags_bagNamesEquals() {
        val bag1 = Bag(3, "Bag1")
        val bag2 = Bag(4, "Bag2")
        val set = MatchesBoxSet(5, "Set1", bag1.id)
        val box = MatchesBox(6, "Box1", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        subject.start(box.id, component.id)

        val bagNames = subject.bagNames.getOrAwaitValue()
        assertThat(bagNames[0], `is`(bag1.name))
        assertThat(bagNames[1], `is`(bag2.name))
    }

    @Test
    fun boxId_boxSelectedIndexEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box1 = MatchesBox(1, "Box1", set.id) // index 0
        val box2 = MatchesBox(2, "Box2", set.id) // index 1
        val box3 = MatchesBox(3, "Box3", set.id) // index 2 selected
        val box4 = MatchesBox(4, "Box4", set.id) // index 3
        val component = RadioComponent(1, "Component", 3, box1.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component)
        subject.start(box3.id, component.id)

        val selectedIndex = subject.boxSelectedIndex.getOrAwaitValue()
        assertThat(selectedIndex, `is`(2))
    }

    @Test
    fun boxId_setSelectedIndexEquals() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val set3 = MatchesBoxSet(3, "Set3", bag.id) // selected index 2
        val set4 = MatchesBoxSet(4, "Set4", bag.id)
        val box = MatchesBox(1, "Box", set3.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(box.id, component.id)

        val selectedIndex = subject.setSelectedIndex.getOrAwaitValue()
        assertThat(selectedIndex, `is`(2))
    }

    @Test
    fun getBags_selectedIndexEquals() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val bag3 = Bag(3, "Bag3") // selected index 2
        val bag4 = Bag(4, "Bag4")
        val set = MatchesBoxSet(1, "Set", bag3.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag1, bag2, bag3, bag4)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(box.id, component.id)

        val selectedIndex = subject.bagSelectedIndex.getOrAwaitValue()
        assertThat(selectedIndex, `is`(2))
    }

    @Test
    fun setSelected_boxNamesChanged() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id) // new selected, index 0
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component = RadioComponent(1, "Component", 3, box4.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component)
        subject.start(box4.id, component.id)

        subject.setSelected(0)

        val boxNames = subject.boxNames.getOrAwaitValue()
        assertThat(boxNames[0], `is`(box1.name))
    }

    @Test
    fun bagSelected_setsAndBoxesChanged() {
        val bag1 = Bag(1, "Bag1") // selected, index 0
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        val component = RadioComponent(1, "Component", 4, box5.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        dataSource.addRadioComponents(component)
        subject.start(box5.id, component.id)

        subject.bagSelected(0)

        val setNames = subject.setNames.getOrAwaitValue()
        val boxNames = subject.boxNames.getOrAwaitValue()
        assertThat(setNames[0], `is`(set1.name))
        assertThat(boxNames[0], `is`(box1.name))
    }
}