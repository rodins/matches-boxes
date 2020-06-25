package com.sergeyrodin.matchesboxes.bag.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BagsListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: BagsListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = BagsListViewModel(dataSource)
    }

    @Test
    fun addItems_sizeEquals() {
        dataSource.addBags(
            Bag(1, "Bag1"),
            Bag(2, "Bag2"),
            Bag(3, "Bag3"),
            Bag(4, "Bag4")
        )
        val bags = subject.bagsList.getOrAwaitValue()

        assertThat(bags.size, `is`(4))
    }

    @Test
    fun noItems_sizeZero() {
        dataSource.addBags()

        val bags = subject.bagsList.getOrAwaitValue()

        assertThat(bags.size, `is`(0))
    }

    @Test
    fun noItems_noItemsTextVisible_isTrue() {
        dataSource.addBags()
        val noItemsTextVisible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(noItemsTextVisible, `is`(true))
    }

    @Test
    fun addItems_noItemsTextVisible_isFalse() {
        dataSource.addBags(
            Bag(1, "Bag1"),
            Bag(2, "Bag2"),
            Bag(3, "Bag3"),
            Bag(4, "Bag4")
        )

        val noItemsTextVisible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(noItemsTextVisible, `is`(false))
    }

    @Test
    fun addItem_eventNotNull() {
        dataSource.addBags()

        subject.addItem()

        val event = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectItem_eventNumberMatches() {
        dataSource.addBags()

        subject.selectItem(1)

        val value = subject.selectItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(value, `is`(1))
    }

    @Test
    fun bagsList_quantityEquals() {
        val bag1 = Bag(1, "Bag1")
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
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        val component9 = RadioComponent(9, "Component9", 9, box5.id)
        val component10 = RadioComponent(10, "Component10", 10, box5.id)
        val component11 = RadioComponent(11, "Component11", 11, box6.id)
        val component12 = RadioComponent(12, "Component12", 12, box6.id)
        val component13 = RadioComponent(13, "Component13", 13, box7.id)
        val component14 = RadioComponent(14, "Component14", 14, box7.id)
        val component15 = RadioComponent(15, "Component15", 15, box8.id)
        val component16 = RadioComponent(16, "Component16", 16, box8.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        dataSource.addRadioComponents(component1, component2, component3, component4, component5,
            component6, component7, component8, component9, component10, component11, component12,
            component13, component14, component15, component16)

        val items = subject.bagsList.getOrAwaitValue()
        assertThat(items[0].componentsQuantity, `is`("36"))
        assertThat(items[1].componentsQuantity, `is`("100"))
    }
}