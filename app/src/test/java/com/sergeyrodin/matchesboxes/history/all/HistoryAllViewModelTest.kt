package com.sergeyrodin.matchesboxes.history.all

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
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
        subject = HistoryAllViewModel(dataSource)

        val list = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(list.size, `is`(4))
    }

    @Test
    fun noItems_sizeZero() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()
        dataSource.addHistory()
        subject = HistoryAllViewModel(dataSource)

        val list = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(list.size, `is`(0))
    }

    @Test
    fun noItems_noItemsTextVisibleTrue() {
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        dataSource.addRadioComponents()
        dataSource.addHistory()
        subject = HistoryAllViewModel(dataSource)

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
        subject = HistoryAllViewModel(dataSource)

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
        subject = HistoryAllViewModel(dataSource)

        val list = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(list[0].title, `is`(component.name))
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
        subject = HistoryAllViewModel(dataSource)

        val list = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(list[0].subTitle, `is`("середа лип.-29-2020 08:13"))
    }

    @Test
    fun deleteHistory() {
        val boxId = 1
        val position = 0
        val component1 = RadioComponent(1, "Component1", 2, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.deleteHighlightedPresentation()

        val list = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(list[0].title, `is`(component2.name))
    }

    @Test
    fun deleteHistoryItem_actionDeleteVisibilityEventFalse() {
        val boxId = 1
        val position = 0
        val component1 = RadioComponent(1, "Component1", 2, boxId)
        val component2 = RadioComponent(2, "Component2", 3, boxId)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.deleteHighlightedPresentation()

        val actionDeleteVisibility = subject.actionModeEvent.getOrAwaitValue()
        assertThat(actionDeleteVisibility, `is`(false))
    }

    @Test
    fun itemClick_notDeleteMode_selectedEventNotNull() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationClick(position)

        val event = subject.selectedEvent.getOrAwaitValue()
        assertThat(event, `is`(not(nullValue())))
        assertThat(event.peekContent().componentId, `is`(component.id))
        assertThat(event.peekContent().name, `is`(component.name))
    }

    @Test
    fun longClick_notDeleteMode_itemHighlighted() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].isHighlighted, `is`(true))
    }

    @Test
    fun highlightedItemClick_deleteMode_itemNotHighlighted() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.presentationClick(position)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].isHighlighted, `is`(false))
    }

    @Test
    fun clickOnNotHighlightedItem_highlightedItemIsNotHighlighted() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.presentationClick(position)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[1].isHighlighted, `is`(false))
    }

    @Test
    fun itemLongClick_actionDeleteEventTrue() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)

        val deleteVisible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(deleteVisible, `is`(true))
    }

    @Test
    fun clickOnHighlighted_actionDeleteEventFalse() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.presentationClick(position)

        val deleteVisible = subject.actionModeEvent.getOrAwaitValue()
        assertThat(deleteVisible, `is`(false))
    }

    @Test
    fun presentationLongClick_itemChangedEventEquals() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)

        val changedItemPosition = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedItemPosition, `is`(position))
    }

    @Test
    fun presentationClick_itemChangedEventEquals() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)

        val changedItemPosition1 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedItemPosition1, `is`(position))

        subject.presentationClick(position)

        val changedItemPosition2 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedItemPosition2, `is`(position))
    }

    @Test
    fun oneComponent_deltaIsEmpty() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        val presentation = subject.historyPresentationItems.getOrAwaitValue()[0]
        assertThat(presentation.delta, `is`(""))
    }

    @Test
    fun oneComponent_twoHistories_positiveDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity+5)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject = HistoryAllViewModel(dataSource)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`("+5"))
        assertThat(items[1].delta, `is`(""))
    }

    @Test
    fun oneComponent_twoHistories_negativeDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity-3)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject = HistoryAllViewModel(dataSource)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`("-3"))
        assertThat(items[1].delta, `is`(""))
    }

    @Test
    fun twoComponents_fourHistories_positiveDeltaEquals() {
        val boxId = 1
        val component1 = RadioComponent(1,"Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 7, boxId)
        val history1 = History(1, component1.id, component1.quantity+3)
        val history2 = History(2, component1.id, component1.quantity)
        val history3 = History(3, component2.id, component2.quantity+5)
        val history4 = History(4, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4)
        subject = HistoryAllViewModel(dataSource)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`("+3"))
        assertThat(items[1].delta, `is`(""))
        assertThat(items[2].delta, `is`("+5"))
        assertThat(items[3].delta, `is`(""))
    }

    @Test
    fun twoComponents_fourHistories_negativeDeltaEquals() {
        val boxId = 1
        val component1 = RadioComponent(1,"Component1", 3, boxId)
        val component2 = RadioComponent(2, "Component2", 7, boxId)
        val history1 = History(1, component1.id, component1.quantity-3)
        val history2 = History(2, component1.id, component1.quantity)
        val history3 = History(3, component2.id, component2.quantity-5)
        val history4 = History(4, component2.id, component2.quantity)
        dataSource.addRadioComponents(component1, component2)
        dataSource.addHistory(history1, history2, history3, history4)
        subject = HistoryAllViewModel(dataSource)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`("-3"))
        assertThat(items[1].delta, `is`(""))
        assertThat(items[2].delta, `is`("-5"))
        assertThat(items[3].delta, `is`(""))
    }

    @Test
    fun oneComponentTwoHistoriesEqual_noDeltaDisplayed() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject = HistoryAllViewModel(dataSource)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`(""))
        assertThat(items[1].delta, `is`(""))
    }

    @Test
    fun actionModeClosed_itemNotHighlighted() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.actionModeClosed()

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].isHighlighted, `is`(false))
    }

    @Test
    fun actionModeClosedByItemClick_actionModeEventFalse() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.presentationClick(position)

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
        subject = HistoryAllViewModel(dataSource)

        subject.presentationLongClick(position)
        subject.actionModeClosed()

        val active = subject.actionModeEvent.getOrAwaitValue()
        assertThat(active, `is`(false))
    }
}