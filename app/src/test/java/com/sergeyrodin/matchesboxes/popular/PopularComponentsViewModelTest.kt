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

    @Test
    fun moreComponents_twentyPopularPlaces_sizeEquals() {
        val maxPlaces = 20
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 4, boxId)
        val component3 = RadioComponent(3,"Component3", 4, boxId)
        val component4 = RadioComponent(4,"Component3", 4, boxId)
        val component5 = RadioComponent(5,"Component3", 4, boxId)
        val component6 = RadioComponent(6,"Component3", 4, boxId)
        val component7 = RadioComponent(7,"Component3", 4, boxId)
        val component8 = RadioComponent(8,"Component3", 4, boxId)
        val component9 = RadioComponent(9,"Component3", 4, boxId)
        val component10 = RadioComponent(10,"Component3", 4, boxId)
        val component11 = RadioComponent(11,"Component3", 4, boxId)
        val component12 = RadioComponent(12,"Component3", 4, boxId)
        val component13 = RadioComponent(13,"Component3", 4, boxId)
        val component14 = RadioComponent(14,"Component3", 4, boxId)
        val component15 = RadioComponent(15,"Component3", 4, boxId)
        val component16 = RadioComponent(16,"Component3", 4, boxId)
        val component17 = RadioComponent(17,"Component3", 4, boxId)
        val component18 = RadioComponent(18,"Component3", 4, boxId)
        val component19 = RadioComponent(19,"Component3", 4, boxId)
        val component20 = RadioComponent(20,"Component3", 4, boxId)
        val component21 = RadioComponent(21,"Component3", 4, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component3.id, component3.quantity)
        val history4 = History(4, component4.id, component4.quantity)
        val history5 = History(5, component5.id, component5.quantity)
        val history6 = History(6, component6.id, component6.quantity)
        val history7 = History(7, component7.id, component7.quantity)
        val history8 = History(8, component8.id, component8.quantity)
        val history9 = History(9, component9.id, component9.quantity)
        val history10 = History(10, component10.id, component10.quantity)
        val history11 = History(11, component11.id, component11.quantity)
        val history12 = History(12, component12.id, component12.quantity)
        val history13 = History(13, component13.id, component13.quantity)
        val history14 = History(14, component14.id, component14.quantity)
        val history15 = History(15, component15.id, component15.quantity)
        val history16 = History(16, component16.id, component16.quantity)
        val history17 = History(17, component17.id, component17.quantity)
        val history18 = History(18, component18.id, component18.quantity)
        val history19 = History(19, component19.id, component19.quantity)
        val history20 = History(20, component20.id, component20.quantity)
        val history21 = History(21, component21.id, component21.quantity)
        dataSource.addRadioComponents(component1, component2, component3, component4, component5,
        component6, component7, component8, component9, component10, component11, component12,
        component13, component14, component15, component16, component17, component18, component19,
        component20, component21)
        dataSource.addHistory(history1, history2, history3, history4, history5, history6, history7,
        history8, history9, history10, history11, history12, history13, history14, history15, history16,
        history17, history18, history19, history20, history21)
        subject = PopularComponentsViewModel(dataSource)

        val items = subject.popularItems.getOrAwaitValue()
        assertThat(items.size, `is`(20))
    }

    @Test
    fun noComponents_noComponentsTextVisibleTrue() {
        subject = PopularComponentsViewModel(dataSource)

        val visible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(visible, `is`(true))
    }

    @Test
    fun oneComponent_noComponentTextVisibleFalse() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = PopularComponentsViewModel(dataSource)

        val visible = subject.noComponentsTextVisible.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }
}