package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HihgligtedPositionSaverAndNotifier
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HistoryAllViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel() {
    private val highlightedPositionSaver = HihgligtedPositionSaverAndNotifier()
    private val deltaCalculator = DeltaCalculator()

    private lateinit var historyItems: List<History>

    private val _historyPresentationItems = MutableLiveData<List<HistoryPresentation>>()
    val historyPresentationItems: LiveData<List<HistoryPresentation>>
        get() = _historyPresentationItems

    val noHistoryTextVisible = historyPresentationItems.map {
        it.isEmpty()
    }

    private val _selectedEvent = MutableLiveData<Event<HistoryPresentation>>()
    val selectedEvent: LiveData<Event<HistoryPresentation>>
        get() = _selectedEvent

    private val _actionDeleteVisibilityEvent = MutableLiveData<Event<Boolean>>()
    val actionDeleteVisibilityEvent: LiveData<Event<Boolean>>
        get() = _actionDeleteVisibilityEvent

    val itemChangedEvent = highlightedPositionSaver.itemChangedEvent
    private val componentNames = mutableMapOf<Int, String>()

    init {
        viewModelScope.launch {
            getAndConvertHistoryItems()
        }
    }

    private suspend fun getAndConvertHistoryItems() {
        getHistoryItemsFromDb()
        deltaCalculator.calculateDeltasForHistoryItems(historyItems)
        convertHistoryItemsToHistoryPresentationItems()
    }

    private suspend fun getHistoryItemsFromDb() {
        historyItems = dataSource.getHistoryList()
    }

    private suspend fun convertHistoryItemsToHistoryPresentationItems() {
        val presentationItems = historyItems.map { history ->
            HistoryPresentation(
                history.id,
                history.componentId,
                getComponentName(history),
                history.quantity.toString(),
                convertLongToDateString(history.date),
                deltaCalculator.getDeltaByHistoryId(history.id)
            )
        }
        updatePresentationItems(presentationItems)
    }

    private suspend fun getComponentName(history: History): String {
        return getComponentNameFromCache(history) ?: getComponentNameFromDatabase(history)
    }

    private fun getComponentNameFromCache(history: History) =
        componentNames[history.componentId]

    private suspend fun getComponentNameFromDatabase(history: History): String {
        val component = dataSource.getRadioComponentById(history.componentId)
        val name = component?.name ?: ""
        componentNames[history.componentId] = name
        return name
    }

    private fun updatePresentationItems(presentationItems: List<HistoryPresentation>) {
        _historyPresentationItems.value = presentationItems
    }

    fun presentationClick(presentation: HistoryPresentation) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            callSelectedEvent(presentation)
        } else {
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
        if (highlightedPositionSaver.isNotHighlightMode()) {
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

    fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            val history = findHistoryByHighlightedPresentationId()
            deleteHistory(history)
            highlightedPositionSaver.resetHighlightedPositionAfterDelete()
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

class HistoryAllViewModelFactory(private val dataSource: RadioComponentsDataSource) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryAllViewModel::class.java)) {
            return HistoryAllViewModel(dataSource) as T
        } else {
            throw IllegalArgumentException("No view mode class found.")
        }
    }
}