package com.sergeyrodin.matchesboxes.history.component

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.MainCoroutineRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
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

        val list = subject.historyPresentationItems.getOrAwaitValue()
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

        val list = subject.historyPresentationItems.getOrAwaitValue()
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

        val item = subject.historyPresentationItems.getOrAwaitValue()[0]
        assertThat(item.title, `is`(convertLongToDateString(history.date)))
    }

    @Test
    fun longPresentationClick_itemHighlighted() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val item = subject.historyPresentationItems.getOrAwaitValue()[0]
        assertThat(item.isHighlighted, `is`(true))
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

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val changedPosition = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedPosition, `is`(position))
    }

    @Test
    fun longClickOnSecondElement_secondElementNotHighligted() {
        val boxId = 1
        val position1 = 0
        val position2 = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(2))

        subject.presentationLongClick(position1)
        subject.presentationLongClick(position2)

        val item = subject.historyPresentationItems.getOrAwaitValue()[position2]
        assertThat(item.isHighlighted, `is`(false))
    }

    @Test
    fun presentationClick_highlightedItemNotHighlighted() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        subject.presentationClick()

        val item = subject.historyPresentationItems.getOrAwaitValue()[position]
        assertThat(item.isHighlighted, `is`(false))
    }

    @Test
    fun presentationClick_itemChangedEventEquals() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val changedPosition1 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedPosition1, `is`(position))

        subject.presentationClick()

        val changedPosition2 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedPosition2, `is`(position))
    }

    @Test
    fun presentationClick_noHighlightedItem_itemEventChangedIsNull() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationClick()

        try{
            subject.itemChangedEvent.getOrAwaitValue()
            fail()
        }catch(e: TimeoutException) {

        }
    }

    @Test
    fun twoPresentationClicks_secondItemChangedEventIsNull() {
        val boxId = 1
        val position = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val changedPosition1 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedPosition1, `is`(position))

        subject.presentationClick()

        val changedPosition2 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedPosition2, `is`(position))

        subject.presentationClick()

        val changedPosition3 = subject.itemChangedEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(changedPosition3, `is`(nullValue()))
    }

    @Test
    fun deleteHighlightedPresentation_sizeEquals() {
        val boxId = 1
        val position1 = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(2))

        subject.presentationLongClick(position1)

        subject.deleteHighlightedPresentation()

        val items2 = subject.historyPresentationItems.getOrAwaitValue()
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

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)

        val visible = subject.actionDeleteVisibleEvent.getOrAwaitValue().getContentIfNotHandled()
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

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)
        subject.presentationClick()

        val visible = subject.actionDeleteVisibleEvent.getOrAwaitValue().getContentIfNotHandled()
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

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(1))

        subject.presentationLongClick(position)
        subject.deleteHighlightedPresentation()

        val visible = subject.actionDeleteVisibleEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(visible, `is`(false))
    }

    @Test
    fun highlightItemAfterDelete_itemHighlighted() {
        val boxId = 1
        val position1 = 0
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items.size, `is`(2))

        subject.presentationLongClick(position1)

        subject.deleteHighlightedPresentation()

        val items1 = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items1.size, `is`(1))

        subject.presentationLongClick(position1)

        val items2 = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items2[0].isHighlighted, `is`(true))
    }

    @Test
    fun twoHistories_positiveDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity+5)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`("+5"))
        assertThat(items[1].delta, `is`(""))
    }

    @Test
    fun twoHistories_negativeDeltaEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity-5)
        val history2 = History(2, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1, history2)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`("-5"))
        assertThat(items[1].delta, `is`(""))
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

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`(""))
        assertThat(items[1].delta, `is`(""))
    }

    @Test
    fun oneHistory_deltaEmpty() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 3, boxId)
        val history1 = History(1, component.id, component.quantity)
        dataSource.addRadioComponents(component)
        dataSource.addHistory(history1)
        subject.start(component.id)

        val items = subject.historyPresentationItems.getOrAwaitValue()
        assertThat(items[0].delta, `is`(""))
    }
}