package com.sergeyrodin.matchesboxes.common.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CommonViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: CommonViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = CommonViewModel(dataSource)
    }

    // Bags

    @Test
    fun addBags_sizeEquals() {
        dataSource.addBags(
            Bag(1, "Bag1"),
            Bag(2, "Bag2"),
            Bag(3, "Bag3"),
            Bag(4, "Bag4")
        )
        dataSource.addRadioComponents()
        val bags = subject.bagsList.getOrAwaitValue()

        assertThat(bags.size, `is`(4))
    }

    @Test
    fun noBags_sizeZero() {
        dataSource.addBags()
        dataSource.addRadioComponents()
        val bags = subject.bagsList.getOrAwaitValue()

        assertThat(bags.size, `is`(0))
    }

    @Test
    fun noBags_noBagsTextVisible_isTrue() {
        dataSource.addBags()
        dataSource.addRadioComponents()
        val noItemsTextVisible = subject.isNoBagsTextVisible.getOrAwaitValue()

        assertThat(noItemsTextVisible, `is`(true))
    }

    @Test
    fun addBags_noBagsTextVisible_isFalse() {
        dataSource.addBags(
            Bag(1, "Bag1"),
            Bag(2, "Bag2"),
            Bag(3, "Bag3"),
            Bag(4, "Bag4")
        )
        dataSource.addRadioComponents()
        val noItemsTextVisible = subject.isNoBagsTextVisible.getOrAwaitValue()

        assertThat(noItemsTextVisible, `is`(false))
    }

    @Test
    fun addBag_eventNotNull() {
        dataSource.addBags()

        subject.addBag()

        val event = subject.addBagEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
    fun selectBag_eventNumberMatches() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        dataSource.addRadioComponents()
        subject.bagsList.getOrAwaitValue()

        subject.selectBag(bag.id)

        val id = subject.selectBagEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(id, `is`(bag.id))
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

    @Test
    fun bagSelected_bagNameEquals() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        dataSource.addRadioComponents()
        subject.bagsList.getOrAwaitValue()

        subject.selectBag(bag.id)

        assertThat(subject.bagName, `is`(bag.name))
    }

    // Sets

    @Test
    fun bagAndNoMatchesBoxSets_sizeZero() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.startSet(bag.id)

        val list = subject.setsList.getOrAwaitValue()

        assertThat(list.size, `is`(0))
    }

    @Test
    fun noMatchesBoxSetsTextVisible_equalsTrue() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.startSet(bag.id)

        val visible = subject.isNoSetsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun bagAndFewMatchesBoxSets_sizeFew() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bag.id),
            MatchesBoxSet(2, "MBS2", bag.id),
            MatchesBoxSet(3, "MBS3", bag.id)
        )
        subject.startSet(bag.id)

        val list = subject.setsList.getOrAwaitValue()

        assertThat(list.size, `is`(3))
    }

    @Test
    fun fewSets_noSetsText_equalsFalse() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bag.id),
            MatchesBoxSet(2, "MBS2", bag.id),
            MatchesBoxSet(3, "MBS3", bag.id)
        )
        subject.startSet(bag.id)

        val visible = subject.isNoSetsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addSet_eventNotNull() {
        subject.addSet()

        val event = subject.addSetEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(event, `is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
    fun selectSet_evenNumberSelected() = runBlocking{
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val set3 = MatchesBoxSet(3, "Set3", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2, set3)
        dataSource.addRadioComponents()
        subject.startSet(bag.id)

        subject.selectSet(set2.id)

        val id = subject.selectSetEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(id, `is`(set2.id))
    }

    @Test
    fun setsList_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component1, component2, component3, component4,
            component5, component6, component7, component8)
        subject.startSet(bag.id)

        val items = subject.setsList.getOrAwaitValue()
        assertThat(items[0].componentsQuantity, `is`("10"))
        assertThat(items[1].componentsQuantity, `is`("26"))
    }

    @Test
    fun selectSet_setNameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val set3 = MatchesBoxSet(3, "Set3", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2, set3)
        dataSource.addRadioComponents()
        subject.startSet(bag.id)

        subject.selectSet(set2.id)

        assertThat(subject.setName, `is`(set2.name))
    }

    // Boxes

    @Test
    fun fewBoxes_sizeEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val set2Id = 2
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", set.id),
            MatchesBox(2, "Box2", set.id),
            MatchesBox(3, "Box3", set.id),
            MatchesBox(4, "Box4", set2Id)
        )
        subject.startBox(set.id)

        val items = subject.boxesList.getOrAwaitValue()
        assertThat(items.size, `is`(3))
    }

    @Test
    fun noBoxes_sizeZero() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.startBox(set.id)

        val items = subject.boxesList.getOrAwaitValue()

        assertThat(items.size, `is`(0))
    }

    @Test
    fun noBoxes_noItemsTextVisibleIsTrue() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.startBox(set.id)

        val visible = subject.isNoBoxesTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun fewBoxes_noItemsTextVisibleIsFalse() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", set.id),
            MatchesBox(2, "Box2", set.id),
            MatchesBox(3, "Box3", set.id)
        )
        subject.startBox(set.id)

        val visible = subject.isNoBoxesTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addBox_eventNotNull() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.startBox(set.id)

        subject.addBox()

        val event = subject.addBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
    fun selectBox_idEquals() = runBlocking{
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box = MatchesBox(2, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        subject.startBox(set.id)

        subject.selectBox(box.id)

        val id = subject.selectBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(id, `is`(box.id))
    }

    @Test
    fun boxesInput_quantitiesEqual(){
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box1 = MatchesBox(1, "Box1", set.id)
        val box2 = MatchesBox(2, "Box2", set.id)
        val component1 = RadioComponent(1, "Component1", 3, box1.id)
        val component2 = RadioComponent(2, "Component2", 4, box1.id)
        val component3 = RadioComponent(3, "Component3", 5, box2.id)
        val component4 = RadioComponent(4, "Component4", 6, box2.id)
        dataSource.addMatchesBoxes(box1, box2)
        dataSource.addRadioComponents(component1, component2, component3, component4)
        subject.startBox(set.id)

        val items = subject.boxesList.getOrAwaitValue()
        assertThat(items[0].componentsQuantity, `is`("7"))
        assertThat(items[1].componentsQuantity, `is`("11"))
    }

    // Components

    @Test
    fun noComponents_sizeZero() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        subject.startComponent(box.id)

        val items = subject.componentsList.getOrAwaitValue()

        assertThat(items.size, `is`(0))
    }

    @Test
    fun fewComponents_sizeEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 2, box.id),
            RadioComponent(2, "Component2", 2, box.id),
            RadioComponent(3, "Component3", 3, box.id)
        )
        subject.startComponent(box.id)

        val items = subject.componentsList.getOrAwaitValue()

        assertThat(items.size, `is`(3))
    }

    @Test
    fun noComponents_noItemsTextVisibleTrue() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        subject.startComponent(box.id)

        val visible = subject.noComponentsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun fewComponents_noItemsTextVisibleFalse() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents(
            RadioComponent(1, "Component1", 2, box.id),
            RadioComponent(2, "Component2", 2, box.id),
            RadioComponent(3, "Component3", 3, box.id)
        )
        subject.startComponent(box.id)

        val visible = subject.noComponentsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addComponent_eventNotNull() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        dataSource.addRadioComponents()
        subject.startComponent(box.id)

        subject.addComponent()

        val value = subject.addComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(value, `is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

}