package com.sergeyrodin.matchesboxes.history.component

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

class ComponentHistoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: ComponentHistoryViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = ComponentHistoryViewModel(dataSource)
    }

    @Test
    fun fewHistoryItems_sizeEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3, box.id)
        val component2 = RadioComponent(2, "Component2", 3, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component1.id, component1.quantity)
        val history3 = History(3, component1.id, component1.quantity)
        val history4 = History(4, component2.id, component2.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4)

        subject.start(component1.id)

        val list = subject.historyItems.getOrAwaitValue()
        assertThat(list.size, `is`(3))
    }

    @Test
    fun noHistoryItems_sizeZero() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory()

        subject.start(component.id)

        val list = subject.historyItems.getOrAwaitValue()
        assertThat(list.size, `is`(0))
    }

    @Test
    fun noHistoryItems_noItemsTextVisibleTrue() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory()

        subject.start(component.id)

        val visible = subject.noItemsTextVisible.getOrAwaitValue()
        assertThat(visible, `is`(true))
    }

    @Test
    fun fewHistoryItems_noItemsTextVisibleFalse() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3, box.id)
        val component2 = RadioComponent(2, "Component2", 3, box.id)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component1.id, component1.quantity)
        val history3 = History(3, component1.id, component1.quantity)
        val history4 = History(4, component2.id, component2.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4)

        subject.start(component1.id)

        val visible = subject.noItemsTextVisible.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }

    @Test
    fun oneItem_dateEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        val history = History(1, component.id, component.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items[0].date, `is`(history.date))
    }

    @Test
    fun longPresentationClick_itemChangedEventEquals() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val changedPosition = subject.highlightedItemIdEvent.getOrAwaitValue()
        assertThat(changedPosition, `is`(position))
    }

    @Test
    fun presentationClick_itemChangedEventEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(history.id)

        val changedId1 = subject.highlightedItemIdEvent.getOrAwaitValue()
        assertThat(changedId1, `is`(history.id))

        subject.presentationClick()

        val changedId2 = subject.highlightedItemIdEvent.getOrAwaitValue()
        assertThat(changedId2, `is`(-1))
    }

    @Test
    fun presentationClick_noHighlightedItem_itemEventChangedIsNull() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationClick()

        try{
            subject.highlightedItemIdEvent.getOrAwaitValue()
            fail()
        }catch(e: TimeoutException) {

        }
    }

    @Test
    fun twoPresentationClicks_secondItemChangedEventEqualsPosition() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(history.id)

        val changedId1 = subject.highlightedItemIdEvent.getOrAwaitValue()
        assertThat(changedId1, `is`(history.id))

        subject.presentationClick()

        val changedId2 = subject.highlightedItemIdEvent.getOrAwaitValue()
        assertThat(changedId2, `is`(-1))

        subject.presentationClick()

        val changedId3 = subject.highlightedItemIdEvent.getOrAwaitValue()
        assertThat(changedId3, `is`(-1))
    }

    @Test
    fun deleteHighlightedPresentation_sizeEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(2))

        subject.presentationLongClick(history1.id)

        subject.deleteHighlightedPresentation()

        val items2 = subject.historyItems.getOrAwaitValue()
        assertThat(items2.size, `is`(1))
        assertThat(items2[0].id, `is`(history2.id))
    }

    @Test
    fun itemHighlighted_actionDeleteVisibleEventTrue() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val visible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(visible, `is`(true))
    }

    @Test
    fun itemNotHighlighted_actionDeleteVisibleEventFalse() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)
        subject.presentationClick()

        val visible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }

    @Test
    fun deleteItem_actionDeleteVisibleFalse() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)
        subject.deleteHighlightedPresentation()

        val visible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }

    @Test
    fun twoHistories_positiveDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity+5)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val deltas = subject.deltas.getOrAwaitValue()
        assertThat(deltas[history1.id], `is`(""))
        assertThat(deltas[history2.id], `is`("+5"))
    }

    @Test
    fun twoHistories_negativeDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 11, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity-5)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val deltas = subject.deltas.getOrAwaitValue()
        assertThat(deltas[history1.id], `is`(""))
        assertThat(deltas[history2.id], `is`("-5"))
    }

    @Test
    fun twoHistories_equalQuantities_deltasEmpty() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val deltas = subject.deltas.getOrAwaitValue()
        assertThat(deltas[history1.id], `is`(""))
        assertThat(deltas[history2.id], `is`(""))
    }

    @Test
    fun oneHistory_deltaEmpty() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1)
        subject.start(component.id)

        val deltas = subject.deltas.getOrAwaitValue()
        assertThat(deltas[history1.id], `is`(""))
    }

    @Test
    fun rotateDeviceDeltaCalculationBug() {
        val boxId = 1
        var quantity = 1
        val component = RadioComponent(1, "Component", quantity, boxId)
        val history1 = History(1, component.id, quantity)
        quantity += 11
        val history2 = History(2, component.id, quantity)
        quantity -= 3
        val history3 = History(3, component.id, quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2, history3)

        subject.start(component.id)

        val deltas1 = subject.deltas.getOrAwaitValue()
        assertThat(deltas1[history1.id], `is`(""))
        assertThat(deltas1[history2.id], `is`("+11"))
        assertThat(deltas1[history3.id], `is`("-3"))

        subject.start(component.id)

        val deltas2 = subject.deltas.getOrAwaitValue()
        assertThat(deltas2[history1.id], `is`(""))
        assertThat(deltas2[history2.id], `is`("+11"))
        assertThat(deltas2[history3.id], `is`("-3"))
    }
}