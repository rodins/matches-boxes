package com.sergeyrodin.matchesboxes.history.all

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import com.sergeyrodin.matchesboxes.util.getDeltaById
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HistoryAllViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: HistoryAllViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = HistoryAllViewModel(dataSource)
    }

    @Test
    fun getAllHistory_sizeEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        val history3 = History(3, component.id, component.quantity)
        val history4 = History(4, component.id, component.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2, history3, history4)

        val list = subject.historyItems.getOrAwaitValue()
        assertThat(list.size, `is`(4))
    }

    @Test
    fun noItems_sizeZero() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()
        dataSource.addHistory()

        val list = subject.historyItems.getOrAwaitValue()
        assertThat(list.size, `is`(0))
    }

    @Test
    fun noItems_noItemsTextVisibleTrue() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()
        dataSource.addHistory()

        val visible = subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(visible, `is`(true))
    }

    @Test
    fun fewItems_noItemsTextVisibleFalse() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        val history3 = History(3, component.id, component.quantity)
        val history4 = History(4, component.id, component.quantity)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2, history3, history4)

        val visible = subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }

    @Test
    fun oneItem_componentNameEquals() {
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

        val list = subject.historyItems.getOrAwaitValue()
        assertThat(list[0].name, `is`(component.name))
    }

    @Test
    fun oneItem_historyDateEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        val history = History(1, component.id, component.quantity, 1595999582038L)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(convertLongToDateString(items[0].date), `is`("Wednesday Jul-29-2020 08:13"))
    }

    @Test
    fun deleteHistory() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 2, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)

        subject.presentationLongClick(history1.id)
        subject.deleteHighlightedPresentation()

        val list = subject.historyItems.getOrAwaitValue()
        assertThat(list[0].name, `is`(component2.name))
    }

    @Test
    fun deleteHistoryItem_actionDeleteVisibilityEventFalse() {
        val boxId = 1
        val component1 = RadioComponent(1, "Component1", 2, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)

        subject.presentationLongClick(history1.id)
        subject.deleteHighlightedPresentation()

        val actionDeleteVisibility = subject.actionModeEvent.getOrAwaitValue()
        assertThat(actionDeleteVisibility, `is`(false))
    }

    @Test
    fun itemClick_notDeleteMode_selectedEventNotNull() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.presentationClick(component.id, component.name)

        val componentFromEvent = subject.selectedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(componentFromEvent, `is`(not(nullValue())))
        assertThat(componentFromEvent?.componentId, `is`(component.id))
        assertThat(componentFromEvent?.name, `is`(component.name))
    }

    @Test
    fun itemLongClick_actionDeleteEventTrue() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.presentationLongClick(history.id)

        val deleteVisible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(deleteVisible, `is`(true))
    }

    @Test
    fun clickOnHighlighted_actionDeleteEventFalse() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.presentationLongClick(history.id)
        subject.presentationClick(component.id, component.name)

        val deleteVisible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(deleteVisible, `is`(false))
    }

    @Test
    fun presentationLongClick_itemChangedEventEquals() {
        val position = 0
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(history.id)

        val changedItemPosition = subject.highlightedPositionEvent.getOrAwaitValue()
        assertThat(changedItemPosition, `is`(position))
    }

    @Test
    fun presentationClick_itemChangedEventEquals() {
        val position = 0
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        val items = subject.historyItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(history.id)

        val changedItemPosition1 = subject.highlightedPositionEvent.getOrAwaitValue()
        assertThat(changedItemPosition1, `is`(position))

        subject.presentationClick(component.id, component.name)

        val changedItemPosition2 = subject.highlightedPositionEvent.getOrAwaitValue()
        assertThat(changedItemPosition2, `is`(position))
    }

    @Test
    fun oneComponent_deltaIsEmpty() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(getDeltaById(history.id), `is`(""))
    }

    @Test
    fun oneComponent_twoHistories_positiveDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity+5)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)

        subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(getDeltaById(history1.id), `is`(""))
        assertThat(getDeltaById(history2.id), `is`("+5"))
    }

    @Test
    fun oneComponent_twoHistories_negativeDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity-3)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)

        subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(getDeltaById(history1.id), `is`(""))
        assertThat(getDeltaById(history2.id), `is`("-3"))
    }

    @Test
    fun twoComponents_fourHistories_positiveDeltaEquals() {
        val boxId = 1
        val component1 = RadioComponent(1,"Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 7, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component1.id, component1.quantity+3)
        val history3 = History(3, component2.id, component2.quantity)
        val history4 = History(4, component2.id, component2.quantity+5)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4)

        subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(getDeltaById(history1.id), `is`(""))
        assertThat(getDeltaById(history2.id), `is`("+3"))
        assertThat(getDeltaById(history3.id), `is`(""))
        assertThat(getDeltaById(history4.id), `is`("+5"))
    }

    @Test
    fun twoComponents_fourHistories_negativeDeltaEquals() {
        val boxId = 1
        val component1 = RadioComponent(1,"Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 7, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component1.id, component1.quantity-3)
        val history3 = History(3, component2.id, component2.quantity)
        val history4 = History(4, component2.id, component2.quantity-5)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4)

        subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(getDeltaById(history1.id), `is`(""))
        assertThat(getDeltaById(history2.id), `is`("-3"))
        assertThat(getDeltaById(history3.id), `is`(""))
        assertThat(getDeltaById(history4.id), `is`("-5"))
    }

    @Test
    fun oneComponentTwoHistoriesEqual_noDeltaDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)

        subject.noHistoryTextVisible.getOrAwaitValue()
        assertThat(getDeltaById(history1.id), `is`(""))
        assertThat(getDeltaById(history2.id), `is`(""))
    }

    @Test
    fun actionModeClosedByItemClick_actionModeEventFalse() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.presentationLongClick(history.id)
        subject.presentationClick(component.id, component.name)

        val active = subject.actionModeEvent.getOrAwaitValue()
        assertThat(active, `is`(false))
    }

    @Test
    fun actionModeClosedByActionModeClosed_actionModeEventFalse() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)

        subject.presentationLongClick(position)
        subject.actionModeClosed()

        val active = subject.actionModeEvent.getOrAwaitValue()
        assertThat(active, `is`(false))
    }

    @Test
    fun historyDeleted_deltaEquals() {
        var quantity1 = 3
        var quantity2 = 7
        val boxId = 1
        val component1 = RadioComponent(1,"Component1", quantity1, boxId)
        val component2 = RadioComponent(2, "Component2", quantity2, boxId)

        val history1 = History(1, component1.id, quantity1)
        quantity1 -= 2
        val history2 = History(2, component1.id, quantity1)
        quantity1 += 5
        val history3 = History(3, component1.id, quantity1)

        val history4 = History(4, component2.id, quantity2)
        quantity2 -= 5
        val history5 = History(5, component2.id, quantity2)
        quantity2 += 2
        val history6 = History(6, component2.id, quantity2)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4, history5, history6)

        subject.noHistoryTextVisible.getOrAwaitValue()

        subject.presentationLongClick(history2.id)
        subject.deleteHighlightedPresentation()

        subject.presentationLongClick(history5.id)
        subject.deleteHighlightedPresentation()

        subject.noHistoryTextVisible.getOrAwaitValue()

        assertThat(getDeltaById(history1.id), `is`(""))
        assertThat(getDeltaById(history2.id), `is`(""))
        assertThat(getDeltaById(history3.id), `is`("+3"))
        assertThat(getDeltaById(history4.id), `is`(""))
        assertThat(getDeltaById(history5.id), `is`(""))
        assertThat(getDeltaById(history6.id), `is`("-3"))
    }
}