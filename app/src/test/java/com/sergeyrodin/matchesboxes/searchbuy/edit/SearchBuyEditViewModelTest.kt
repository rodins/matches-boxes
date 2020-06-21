package com.sergeyrodin.matchesboxes.searchbuy.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.MainCoroutineRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchBuyEditViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: SearchBuyEditViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = SearchBuyEditViewModel(dataSource)
    }

    @Test
    fun componentId_componentNameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.componentName.getOrAwaitValue()
        assertThat(name, `is`(component.name))
    }

    @Test
    fun componentId_boxNameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.boxName.getOrAwaitValue()
        assertThat(name, `is`(box.name))
    }

    @Test
    fun componentId_setNameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.matchesBoxSetName.getOrAwaitValue()
        assertThat(name, `is`(set.name))
    }

    @Test
    fun componentId_bagNameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.bagName.getOrAwaitValue()
        assertThat(name, `is`(bag.name))
    }

    @Test
    fun componentId_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val quantity = subject.quantity.getOrAwaitValue()
        assertThat(quantity, `is`(component.quantity.toString()))
    }

    @Test
    fun quantityPlus_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.quantityPlus()

        val quantity = subject.quantity.getOrAwaitValue()
        assertThat(quantity, `is`((component.quantity + 1).toString()))
    }

    @Test
    fun quantityMinus_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.quantityMinus()

        val quantity = subject.quantity.getOrAwaitValue()
        assertThat(quantity, `is`((component.quantity - 1).toString()))
    }

    @Test
    fun quantityZero_minusDisabled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 0,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val disabled = subject.minusEnabled.getOrAwaitValue()
        assertThat(disabled, `is`(false))
    }

    @Test
    fun quantityChanged_saveComponent_quantityEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.quantityPlus()
        subject.saveItem()

        val loaded = dataSource.getRadioComponentById(component.id)
        assertThat(loaded?.quantity, `is`(3))
    }

    @Test
    fun saveComponent_eventNotNull() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.saveItem()

        val saved = subject.saveItemEvent.getOrAwaitValue()
        assertThat(saved, `is`(not(nullValue())))
    }

    @Test
    fun componentIdTrue_isBuyEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id, true)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val isBuy = subject.isBuy.getOrAwaitValue()
        assertThat(isBuy, `is`(component.isBuy))
    }

    @Test
    fun componentIdFalse_isBuyEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val isBuy = subject.isBuy.getOrAwaitValue()
        assertThat(isBuy, `is`(component.isBuy))
    }

    @Test
    fun buyFalseSetTrue_saveItem_buyEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.isBuy.value = true

        subject.saveItem()

        val saved = dataSource.getRadioComponentById(component.id)
        assertThat(saved?.isBuy, `is`(true))
    }

    @Test
    fun quantityNegative_equalsQuantity() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.quantity.value = (-1).toString()

        subject.saveItem()

        val saved = dataSource.getRadioComponentById(component.id)
        assertThat(saved?.quantity, `is`(component.quantity))
    }

    @Test
    fun quantityEmpty_minusEnabledFalse() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.quantity.value = ""

        val enabled = subject.minusEnabled.getOrAwaitValue()
        assertThat(enabled, `is`(false))
    }

    @Test
    fun quantityEmpty_saveItem_quantityZero() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        subject.quantity.value = ""
        subject.saveItem()

        val saved = dataSource.getRadioComponentById(component.id)
        assertThat(saved?.quantity, `is`(0))
    }
}