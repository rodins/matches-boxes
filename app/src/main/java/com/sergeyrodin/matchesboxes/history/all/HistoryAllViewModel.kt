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

    private val _actionModeEvent = MutableLiveData<Boolean>()
    val actionModeEvent: LiveData<Boolean>
        get() = _actionModeEvent

    val itemChangedEvent = highlightedPositionSaver.itemChangedEvent

    init {
        viewModelScope.launch {
            converter.convert()
        }
    }

    fun presentationClick(position: Int) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            navigateToComponentHistory(position)
        } else {
            unhighlightItem()
        }
    }

    private fun navigateToComponentHistory(position: Int) {
        val presentation = getPresentationByPosition(position)
        presentation?.let {
            val componentId = getComponentIdByPresentationId(presentation.id)
            componentId?.let {
                callSelectedEvent(componentId, presentation.title)
            }
        }
    }

    private fun getPresentationByPosition(position: Int): HistoryPresentation? {
        return historyPresentationItems.value?.get(position)
    }

    private fun getComponentIdByPresentationId(id: Int): Int? {
        val history = converter.findHistoryById(id)
        return history?.componentId
    }

    private fun callSelectedEvent(componentId: Int, title: String) {
        val args = SelectedComponentHistoryArgs(componentId, title)
        _selectedEvent.value = Event(args)
    }

    private fun unhighlightItem() {
        highlighter.unhighlight()
        highlightedPositionSaver.notifyChangedAndResetHighlightedPosition()
        callActionModeEvent()
    }

    private fun callActionModeEvent() {
        _actionModeEvent.value = highlightedPositionSaver.isHighlightMode()
    }

    fun actionModeClosed() {
        if(highlightedPositionSaver.isHighlightMode()) {
            highlighter.unhighlight()
            highlightedPositionSaver.notifyChangedAndResetHighlightedPosition()
        }
    }

    fun presentationLongClick(position: Int) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            highlightItemAndSetActionMode(position)
        }
    }

    private fun highlightItemAndSetActionMode(position: Int) {
        highlighter.highlight(position)
        highlightedPositionSaver.saveHighlightedPositionAndNotifyItChanged(position)
        callActionModeEvent()
    }

    fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            deleter.deleteHighlightedPresentation()
            callActionModeEvent()
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
            throw IllegalArgumentException("No view model class found.")
        }
    }
}

data class SelectedComponentHistoryArgs(
    var componentId: Int,
    var name: String
)