package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HistoryPresentationHighlighter
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val highlighter = HistoryPresentationHighlighter()

    private val componentId = MutableLiveData<Int>()
    val historyPresentationItems = componentId.switchMap { id ->
        liveData {
            emit(getComponentHistoryPresentationListByComponentId(id))
        }
    }

    val itemChangedEvent: LiveData<Event<Int>>
        get() = highlighter.itemChangedEvent

    private lateinit var historyItems: List<History>

    private val _actionDeleteVisibleEvent = MutableLiveData<Event<Boolean>>()
    val actionDeleteVisibleEvent: LiveData<Event<Boolean>>
        get() = _actionDeleteVisibleEvent

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
        if(highlighter.isNotHighlightMode()) {
            val presentation = getPresentationByPosition(position)
            setPresentationHighlighted(presentation)
            highlighter.makePositionHighlighted(position)
            setActionDeleteVisible()
        }
    }

    private fun setActionDeleteVisible() {
        _actionDeleteVisibleEvent.value = Event(true)
    }

    private fun getPresentationByPosition(position: Int): ComponentHistoryPresentation? {
        return historyPresentationItems.value?.get(position)
    }

    private fun setPresentationHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = true
    }

    fun presentationClick() {
        if(highlighter.isHighlightMode()) {
            val presentation = getHighlightedPresentation()
            makePresentationNotHighlighted(presentation)
            highlighter.makeHighlightedPositionNotHighlighted()
            setActionDeleteNotVisible()
        }
    }

    private fun getHighlightedPresentation(): ComponentHistoryPresentation? {
        return historyPresentationItems.value?.get(highlighter.highlightedPosition)
    }

    private fun makePresentationNotHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = false
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
                highlighter.resetHighlightModeAfterDelete()
                setActionDeleteNotVisible()
            }
        }
    }

    private fun refreshHistoryItemsFromDb(history: History) {
        start(history.componentId)
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