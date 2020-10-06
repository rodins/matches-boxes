package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighlightedPositionSaverAndNotifier
import com.sergeyrodin.matchesboxes.history.HistoryDeleter
import com.sergeyrodin.matchesboxes.history.HistoryPresentation
import com.sergeyrodin.matchesboxes.history.HistoryPresentationHighlighter
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

    private val _selectedEvent = MutableLiveData<Event<SelectedComponentHistoryArgs>>()
    val selectedEvent: LiveData<Event<SelectedComponentHistoryArgs>>
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
        val history = converter.findHistoryById(presentation.id)
        val componentId = history?.componentId
        componentId?.let{
            val args = SelectedComponentHistoryArgs(componentId, presentation.title)
            _selectedEvent.value = Event(args)
        }
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

data class SelectedComponentHistoryArgs(
    var componentId: Int,
    var name: String
)