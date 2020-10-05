package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighlightedPositionSaverAndNotifier
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HistoryAllViewModel(dataSource: RadioComponentsDataSource) : ViewModel() {
    private val highlightedPositionSaver = HighlightedPositionSaverAndNotifier()
    private val converter = HistoryPresentationConverter(dataSource)
    private val deleter = HistoryDeleter(dataSource, converter, highlightedPositionSaver)
    private val highlighter = HistoryPresentationHighlighter(converter, highlightedPositionSaver)

    val historyPresentationItems = converter.historyPresentationItems
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

    init {
        viewModelScope.launch {
            converter.convert()
        }
    }

    fun presentationClick(presentation: HistoryPresentation) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            callSelectedEvent(presentation)
        } else {
            highlighter.unhighlight()
            highlightedPositionSaver.notifyChangedAndResetHighlightedPosition()
            callActionDeleteVisibilityEvent()
        }
    }

    private fun callSelectedEvent(presentation: HistoryPresentation) {
        _selectedEvent.value = Event(presentation)
    }

    private fun callActionDeleteVisibilityEvent() {
        _actionDeleteVisibilityEvent.value = Event(highlightedPositionSaver.isHighlightMode())
    }

    fun presentationLongClick(position: Int) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            highlighter.highlight(position)
            highlightedPositionSaver.saveHighlightedPositionAndNotifyItChanged(position)
            callActionDeleteVisibilityEvent()
        }
    }

    fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            deleter.deleteHighlightedPresentation()
            callActionDeleteVisibilityEvent()
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