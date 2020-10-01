package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HihgligtedPositionSaverAndNotifier
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HistoryAllViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val highlightedPositionSaver = HihgligtedPositionSaverAndNotifier()

    private lateinit var historyItems: List<History>

    private val _historyPresentationItems = MutableLiveData<List<HistoryPresentation>>()
    val historyPresentationItems: LiveData<List<HistoryPresentation>>
        get() = _historyPresentationItems

    val noHistoryTextVisible = historyPresentationItems.map{
        it.isEmpty()
    }

    private val _selectedEvent = MutableLiveData<Event<HistoryPresentation>>()
    val selectedEvent: LiveData<Event<HistoryPresentation>>
        get() = _selectedEvent

    private val _actionDeleteVisibilityEvent = MutableLiveData<Event<Boolean>>()
    val actionDeleteVisibilityEvent: LiveData<Event<Boolean>>
        get() = _actionDeleteVisibilityEvent

    private val _dataSetChangedEvent = MutableLiveData<Event<Unit>>()
    val dataSetChangedEvent: LiveData<Event<Unit>>
        get() = _dataSetChangedEvent

    val itemChangedEvent = highlightedPositionSaver.itemChangedEvent

    init{
        viewModelScope.launch {
            getAndConvertHistoryItems()
        }
    }

    private suspend fun getAndConvertHistoryItems() {
        getHistoryItemsFromDb()
        convertHistoryItemsToHistoryPresentationItems()
    }

    private suspend fun getHistoryItemsFromDb() {
        historyItems = dataSource.getHistoryList()
    }

    private suspend fun convertHistoryItemsToHistoryPresentationItems() {
        val presentationItems = mutableListOf<HistoryPresentation>()
        historyItems.forEach { history ->
            presentationItems.add(convertHistoryToHistoryPresentation(history))
        }
        updatePresentationItems(presentationItems)
    }

    private suspend fun convertHistoryToHistoryPresentation(history: History): HistoryPresentation {
        val component = dataSource.getRadioComponentById(history.componentId)
        return HistoryPresentation(
            history.id,
            history.componentId,
            component?.name ?: "",
            history.quantity.toString(),
            convertLongToDateString(history.date)
        )
    }

    private fun updatePresentationItems(presentationItems: List<HistoryPresentation>) {
        _historyPresentationItems.value = presentationItems
    }

    fun presentationClick(presentation: HistoryPresentation) {
        if(highlightedPositionSaver.isNotHighlightMode()) {
            callSelectedEvent(presentation)
        }else {
            makeHighlightedPresentationNotHighlighted()
            highlightedPositionSaver.notifyChangedAndResetHighlightedPosition()
            callActionDeleteVisibilityEvent()
        }
    }

    private fun callSelectedEvent(presentation: HistoryPresentation) {
        _selectedEvent.value = Event(presentation)
    }

    private fun makeHighlightedPresentationNotHighlighted() {
        val highlightedPresentation = findHighlightedPresentation()
        setPresentationNotHighlighted(highlightedPresentation)
    }

    private fun findHighlightedPresentation(): HistoryPresentation? {
        return getPresentationByPosition(highlightedPositionSaver.highlightedPosition)
    }

    private fun setPresentationNotHighlighted(presentation: HistoryPresentation?) {
        presentation?.isHighlighted = false
    }

    private fun callActionDeleteVisibilityEvent() {
        _actionDeleteVisibilityEvent.value = Event(highlightedPositionSaver.isHighlightMode())
    }

    fun presentationLongClick(position: Int) {
        if(highlightedPositionSaver.isNotHighlightMode()) {
            val presentation = getPresentationByPosition(position)
            makePresentationHighlighted(presentation)
            highlightedPositionSaver.saveHighlightedPositionAndNotifyItChanged(position)
            callActionDeleteVisibilityEvent()
        }
    }

    private fun getPresentationByPosition(position: Int): HistoryPresentation? {
        return historyPresentationItems.value?.get(position)
    }

    private fun makePresentationHighlighted(presentation: HistoryPresentation?) {
        setPresentationHighlighted(presentation)
    }

    private fun setPresentationHighlighted(presentation: HistoryPresentation?) {
        presentation?.let {
            presentation.isHighlighted = true
        }
    }

    private fun callDataSetChangedEvent() {
        _dataSetChangedEvent.value = Event(Unit)
    }

    fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            val history = findHistoryByHighlightedPresentationId()
            deleteHistory(history)
            highlightedPositionSaver.resetHighlightedPositionAfterDelete()
            callDataSetChangedEvent()
            callActionDeleteVisibilityEvent()
        }
    }

    private fun findHistoryByHighlightedPresentationId(): History? {
        val highlightedPresentation = findHighlightedPresentation()
        return historyItems.find {
            it.id == highlightedPresentation?.id
        }
    }

    private suspend fun deleteHistory(history: History?) {
        history?.let {
            dataSource.deleteHistory(history)
            getAndConvertHistoryItems()
        }
    }
}

class HistoryAllViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryAllViewModel::class.java)) {
            return HistoryAllViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No view mode class found.")
        }
    }
}