package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HistoryAllViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private var highlightedPresentationId = NO_ID_SET
    private lateinit var historyItems: List<History>
    private lateinit var convertedHistoryPresentationItems: MutableList<HistoryPresentation>

    private val _historyPresentationItems = MutableLiveData<List<HistoryPresentation>>()
    val historyPresentationItems: LiveData<List<HistoryPresentation>>
        get() = _historyPresentationItems

    val noHistoryTextVisible = historyPresentationItems.map{
        it.isEmpty()
    }

    private val _selectedEvent = MutableLiveData<Event<HistoryPresentation>>()
    val selectedEvent: LiveData<Event<HistoryPresentation>>
        get() = _selectedEvent

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
        convertedHistoryPresentationItems = mutableListOf()
        historyItems.forEach { history ->
            convertedHistoryPresentationItems.add(convertHistoryToHistoryPresentation(history))
        }
        updatePresentationItems()
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

    fun presentationClick(presentation: HistoryPresentation) {
        if(presentationIsNotHighlighted()) {
            callSelectedEvent(presentation)
        }else {
            makeHighlightedPresentationNotHighlighted()
        }
    }

    private fun callSelectedEvent(presentation: HistoryPresentation) {
        _selectedEvent.value = Event(presentation)
    }

    private fun makeHighlightedPresentationNotHighlighted() {
        val highlightedPresentation = findHighlightedPresentation()
        setPresentationNotHighlighted(highlightedPresentation)
        updatePresentationItems()
    }

    private fun findHighlightedPresentation(): HistoryPresentation? {
        val presentationInConvertedItems = findPresentationById(highlightedPresentationId)
        return presentationInConvertedItems
    }

    private fun setPresentationNotHighlighted(presentationInConvertedItems: HistoryPresentation?) {
        presentationInConvertedItems?.isHighlighted = false
        highlightedPresentationId = NO_ID_SET
    }

    fun presentationLongClick(presentationId: Int) {
        if(presentationIsNotHighlighted()) {
            makePresentationHighlighted(presentationId)
        }
    }

    private fun presentationIsNotHighlighted() = highlightedPresentationId == NO_ID_SET

    private fun makePresentationHighlighted(id: Int) {
        val presentation = findPresentationById(id)
        setPresentationHighlighted(presentation)
        updatePresentationItems()
    }

    private fun findPresentationById(id: Int): HistoryPresentation? {
        return convertedHistoryPresentationItems.find {
            it.id == id
        }
    }

    private fun setPresentationHighlighted(presentationInConvertedItems: HistoryPresentation?) {
        presentationInConvertedItems?.let {
            presentationInConvertedItems.isHighlighted = true
            highlightedPresentationId = presentationInConvertedItems.id
        }
    }

    private fun updatePresentationItems() {
        _historyPresentationItems.value = convertedHistoryPresentationItems
    }

    fun deleteHistory(history: History) {
        viewModelScope.launch {
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