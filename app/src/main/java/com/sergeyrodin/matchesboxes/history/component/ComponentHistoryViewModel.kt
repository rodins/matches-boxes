package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val historyPresentationItems = componentId.switchMap { id ->
        liveData {
            emit(getComponentHistoryPresentationListByComponentId(id))
        }
    }

    private val _itemChangedEvent = MutableLiveData<Event<Int>>()
    val itemChangedEvent: LiveData<Event<Int>>
        get() = _itemChangedEvent

    private var highlightedPosition: Int = NO_ID_SET

    private lateinit var historyItems: List<History>

    private val _actionDeleteVisibleEvent = MutableLiveData<Event<Boolean>>()
    val actionDeleteVisibleEvent: LiveData<Event<Boolean>>
        get() = _actionDeleteVisibleEvent

    private val _dataSetChangedEvent = MutableLiveData<Event<Unit>>()
    val dataSetChangedEvent: LiveData<Event<Unit>>
        get() = _dataSetChangedEvent

    private suspend fun getComponentHistoryPresentationListByComponentId(id: Int): List<ComponentHistoryPresentation> {
        getHistoryItemsFromDb(id)
        return convertHistoryItemsToHistoryPresentationItems()
    }

    private suspend fun getHistoryItemsFromDb(id: Int) {
        historyItems = dataSource.getHistoryListByComponentId(id)
    }

    private fun convertHistoryItemsToHistoryPresentationItems(): List<ComponentHistoryPresentation> {
        return historyItems.map { history ->
            ComponentHistoryPresentation(
                history.id,
                convertLongToDateString(history.date),
                history.quantity.toString()
            )
        }
    }

    val noItemsTextVisible = historyPresentationItems.map { list ->
        list.isEmpty()
    }

    fun start(id: Int) {
        componentId.value = id
    }

    fun presentationLongClick(position: Int) {
        if(isItemNotHighlighted()) {
            val presentation = getPresentationByPosition(position)
            setPresentationHighlighted(presentation)
            setHighlightedPosition(position)
            notifyItemChanged(position)
            setActionDeleteVisible()
        }
    }

    private fun setActionDeleteVisible() {
        _actionDeleteVisibleEvent.value = Event(true)
    }

    private fun isItemNotHighlighted() = highlightedPosition == NO_ID_SET

    private fun getPresentationByPosition(position: Int): ComponentHistoryPresentation? {
        return historyPresentationItems.value?.get(position)
    }

    private fun setPresentationHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = true
    }

    private fun setHighlightedPosition(position: Int) {
        highlightedPosition = position
    }

    private fun notifyItemChanged(position: Int) {
        _itemChangedEvent.value = Event(position)
    }

    fun presentationClick() {
        if(isItemHighlighted()) {
            val presentation = getHighlightedPresentation()
            makePresentationNotHighlighted(presentation)
            notifyItemChangedOnHighlightedPosition()
            resetHighlightedPosition()
            setActionDeleteNotVisible()
        }
    }

    private fun isItemHighlighted() = highlightedPosition != NO_ID_SET

    private fun getHighlightedPresentation(): ComponentHistoryPresentation? {
        return historyPresentationItems.value?.get(highlightedPosition)
    }

    private fun makePresentationNotHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = false
    }

    private fun notifyItemChangedOnHighlightedPosition() {
        _itemChangedEvent.value = Event(highlightedPosition)
    }

    private fun resetHighlightedPosition() {
        highlightedPosition = NO_ID_SET
    }

    private fun setActionDeleteNotVisible() {
        _actionDeleteVisibleEvent.value = Event(false)
    }

    fun deleteHighlightedPresentation() {
        val presentation = getHighlightedPresentation()
        val history = getHighlightedHistoryItem(presentation)
        deleteHistory(history)
    }

    private fun getHighlightedHistoryItem(presentation: ComponentHistoryPresentation?): History? {
        return historyItems.find {
            it.id == presentation?.id
        }
    }

    private fun deleteHistory(history: History?) {
        history?.let {
            viewModelScope.launch {
                dataSource.deleteHistory(history)
                refreshHistoryItemsFromDb(history)
                setActionDeleteNotVisible()
                notifyDataSetChanged()
            }
        }
    }

    private fun refreshHistoryItemsFromDb(history: History) {
        start(history.componentId)
    }

    private fun notifyDataSetChanged() {
        _dataSetChangedEvent.value = Event(Unit)
    }
}

class ComponentHistoryViewModelFactory(private var dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ComponentHistoryViewModel::class.java)) {
            return ComponentHistoryViewModel(dataSource) as T
        }else{
            throw IllegalArgumentException("No ViewModel class found.")
        }
    }
}