package com.sergeyrodin.matchesboxes.popular

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PopularComponentsViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: PopularComponentsViewModel

    @Before
    fun init(){
        dataSource = FakeDataSource()
    }

    @Test
    fun oneComponent_nameEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items[0].name, `is`(component.name))
    }

    @Test
    fun twoComponents_popularityEqual_namesEqual() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items[0].name, `is`(component1.name))
        assertThat(items[1].name, `is`(component2.name))
    }

    @Test
    fun twoComponents_popularityDifferent_namesEqual() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items.size, `is`(2))
        assertThat(items[0].name, `is`(component2.name))
        assertThat(items[1].name, `is`(component1.name))
    }

    @Test
    fun threeComponents_popularityDifferent_namesEqual() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val component3 = RadioComponent(3,"Component3", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component2.id, component2.quantity)
        val history4 = History(4, component2.id, component2.quantity)
        val history5 = History(5, component3.id, component3.quantity)
        val history6 = History(6, component3.id, component3.quantity)
        dataSource.addRadioComponents(component1, component2, component3)
        dataSource.addHistory(history1, history2, history3, history4, history5, history6)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items.size, `is`(3))
        assertThat(items[0].name, `is`(component2.name))
        assertThat(items[1].name, `is`(component3.name))
        assertThat(items[2].name, `is`(component1.name))
    }

    @Test
    fun oneComponent_placeEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items[0].place, `is`("1"))
    }

    @Test
    fun twoComponents_popularityEqual_placesEqual() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items[0].place, `is`("1"))
        assertThat(items[1].place, `is`("2"))
    }

    @Test
    fun twoComponents_popularityDifferent_placesEqual() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items.size, `is`(2))
        assertThat(items[0].place, `is`("1"))
        assertThat(items[1].place, `is`("2"))
    }

    @Test
    fun threeComponents_popularityDifferent_placesEqual() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val component3 = RadioComponent(3,"Component3", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component2.id, component2.quantity)
        val history4 = History(4, component2.id, component2.quantity)
        val history5 = History(5, component3.id, component3.quantity)
        val history6 = History(6, component3.id, component3.quantity)
        dataSource.addRadioComponents(component1, component2, component3)
        dataSource.addHistory(history1, history2, history3, history4, history5, history6)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items.size, `is`(3))
        assertThat(items[0].place, `is`("1"))
        assertThat(items[1].place, `is`("2"))
        assertThat(items[2].place, `is`("3"))
    }
}