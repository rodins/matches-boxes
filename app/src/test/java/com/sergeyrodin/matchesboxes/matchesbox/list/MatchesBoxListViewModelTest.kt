package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchesBoxListViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: MatchesBoxListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = MatchesBoxListViewModel(dataSource)
    }

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
        assertThat(items.size, CoreMatchers.`is`(3))
    }

    @Test
    fun noBoxes_sizeZero() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.startBox(set.id)

        val items = subject.boxesList.getOrAwaitValue()

        assertThat(items.size, CoreMatchers.`is`(0))
    }

    @Test
    fun noBoxes_noItemsTextVisibleIsTrue() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.startBox(set.id)

        val visible = subject.isNoBoxesTextVisible.getOrAwaitValue()

        assertThat(visible, CoreMatchers.`is`(true))
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

        assertThat(visible, CoreMatchers.`is`(false))
    }

    @Test
    fun addBox_eventNotNull() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxes()
        subject.startBox(set.id)

        subject.addBox()

        val event = subject.addBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, CoreMatchers.`is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
    fun selectBox_idEquals() = runBlocking{
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box = MatchesBox(2, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        subject.startBox(set.id)

        subject.selectBox(box.id)

        val loaded = subject.selectBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(loaded?.id, CoreMatchers.`is`(box.id))
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
        assertThat(items[0].componentsQuantity, CoreMatchers.`is`("7"))
        assertThat(items[1].componentsQuantity, CoreMatchers.`is`("11"))
    }
}