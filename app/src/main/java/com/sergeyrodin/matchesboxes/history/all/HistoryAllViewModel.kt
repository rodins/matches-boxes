package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HistoryAllViewModel(dataSource: RadioComponentsDataSource) : ViewModel(),
    HistoryActionModeModel {
    private val highlightedPositionSaver = HighlightedItemIdSaverAndNotifier()
    private val converter = HistoryPresentationConverter(dataSource)
    private val deleter = HistoryDeleter(dataSource, converter, highlightedPositionSaver)

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

    val highlightedIdEvent = highlightedPositionSaver.highlightedItemIdEvent

    init {
        viewModelScope.launch {
            converter.convert()
        }
    }

    fun presentationClick(id: Int) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            navigateToComponentHistory(id)
        } else {
            unhighlightItem()
        }
    }

    private fun navigateToComponentHistory(id: Int) {
        val presentation = getPresentationById(id)
        presentation?.let {
            val componentId = getComponentIdByPresentationId(id)
            componentId?.let {
                callSelectedEvent(componentId, presentation.title)
            }
        }
    }

    private fun getPresentationById(id: Int): HistoryPresentation? {
        return historyPresentationItems.value?.find {
            it.id == id
        }
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
        highlightedPositionSaver.notifyChangedAndResetHighlightedId()
        callActionModeEvent()
    }

    private fun callActionModeEvent() {
        _actionModeEvent.value = highlightedPositionSaver.isHighlightMode()
    }

    override fun actionModeClosed() {
        if(highlightedPositionSaver.isHighlightMode()) {
            unhighlightItem()
        }
    }

    fun presentationLongClick(id: Int) {
        if (highlightedPositionSaver.isNotHighlightMode()) {
            highlightItemAndSetActionMode(id)
        }
    }

    private fun highlightItemAndSetActionMode(id: Int) {
        highlightedPositionSaver.saveHighlightedItemIdAndNotifyItChanged(id)
        callActionModeEvent()
    }

    override fun deleteHighlightedPresentation() {
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