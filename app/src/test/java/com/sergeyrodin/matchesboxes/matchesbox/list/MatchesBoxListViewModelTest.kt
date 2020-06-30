package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchesBoxListViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: MatchesBoxListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = MatchesBoxListViewModel(dataSource)
    }

    @Test
    fun fewItems_sizeEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val set2Id = 2
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", set.id),
            MatchesBox(2, "Box2", set.id),
            MatchesBox(3, "Box3", set.id),
            MatchesBox(4, "Box4", set2Id)
        )
        subject.start(set)

        val items = subject.matchesBoxes.getOrAwaitValue()
        assertThat(items.size, `is`(3))
    }

    @Test
    fun noItems_sizeZero() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.start(set)

        val items = subject.matchesBoxes.getOrAwaitValue()

        assertThat(items.size, `is`(0))
    }

    @Test
    fun noItems_noItemsTextVisibleIsTrue() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.start(set)

        val visible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun fewItems_noItemsTextVisibleIsFalse() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", set.id),
            MatchesBox(2, "Box2", set.id),
            MatchesBox(3, "Box3", set.id)
        )
        subject.start(set)

        val visible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addItem_eventNotNull() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.start(set)

        subject.addMatchesBox()

        val event = subject.addMatchesBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectItem_idEquals() = runBlocking{
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box = MatchesBox(2, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        subject.initBoxes(set.id)
        subject.start(set)

        subject.selectMatchesBox(box.id)

        val selectedBox = subject.selectMatchesBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(selectedBox?.id, `is`(box.id))
    }

    @Test
    fun setId_titleEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set title", bagId)
        dataSource.addMatchesBoxSets(set)
        subject.start(set)

        val title = subject.setTitle.getOrAwaitValue()
        assertThat(title, `is`(set.name))
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
        subject.start(set)

        val items = subject.matchesBoxes.getOrAwaitValue()
        assertThat(items[0].componentsQuantity, `is`("7"))
        assertThat(items[1].componentsQuantity, `is`("11"))
    }
}