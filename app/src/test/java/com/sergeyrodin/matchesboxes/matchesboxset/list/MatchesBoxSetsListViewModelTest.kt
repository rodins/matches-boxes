package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchesBoxSetsListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: MatchesBoxSetsListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = MatchesBoxSetsListViewModel(dataSource)
    }

    @Test
    fun bagAndNoMatchesBoxSets_sizeZero() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag)

        val list = subject.matchesBoxSets.getOrAwaitValue()

        assertThat(list.size, `is`(0))
    }

    @Test
    fun noMatchesBoxSetsTextVisible_equalsTrue() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag)

        val visible = subject.isNoMatchesBoxSetsTextVisible.getOrAwaitValue()

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
        subject.start(bag)

        val list = subject.matchesBoxSets.getOrAwaitValue()

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
        subject.start(bag)

        val visible = subject.isNoMatchesBoxSetsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addItem_eventNotNull() {
        subject.addItem()

        val event = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectItem_evenNumberSelected() = runBlocking{
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val set3 = MatchesBoxSet(3, "Set3", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2, set3)
        dataSource.addRadioComponents()
        subject.start(bag)
        subject.initSets(bag.id)

        subject.selectItem(set2.id)

        val selectedSet = subject.selectItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(selectedSet?.id, `is`(set2.id))
    }

    @Test
    fun bagName_titleEquals(){
        val bag = Bag(1, "Bag Title")
        dataSource.addBags(bag)
        subject.start(bag)

        val title = subject.bagTitle.getOrAwaitValue()

        assertThat(title, `is`(bag.name))
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
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component1, component2, component3, component4,
            component5, component6, component7, component8)
        subject.start(bag)

        val items = subject.matchesBoxSets.getOrAwaitValue()
        assertThat(items[0].componentsQuantity, `is`("10"))
        assertThat(items[1].componentsQuantity, `is`("26"))
    }
}