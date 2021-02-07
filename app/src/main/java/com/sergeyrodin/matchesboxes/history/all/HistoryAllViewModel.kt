package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighlightedItemIdSaverAndNotifier
import com.sergeyrodin.matchesboxes.history.HistoryActionModeModel
import com.sergeyrodin.matchesboxes.util.calculateDeltasForHistoryModelItems
import kotlinx.coroutines.launch

class HistoryAllViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel(),
    HistoryActionModeModel {

    private val highlightedIdSaver = HighlightedItemIdSaverAndNotifier()

    val historyItems = dataSource.observeHistoryModel()

    val noHistoryTextVisible = historyItems.map {
        calculateDeltasForHistoryModelItems(it)
        it.isEmpty()
    }

    private val _selectedEvent = MutableLiveData<Event<SelectedComponentHistoryArgs>>()
    val selectedEvent: LiveData<Event<SelectedComponentHistoryArgs>>
        get() = _selectedEvent

    private val _actionModeEvent = MutableLiveData<Boolean>()
    val actionModeEvent: LiveData<Boolean>
        get() = _actionModeEvent

    val highlightedIdEvent = highlightedIdSaver.highlightedItemIdEvent

    fun presentationClick(componentId: Int, name: String) {
        if (highlightedIdSaver.isNotHighlightMode()) {
            callSelectedEvent(componentId, name)
        } else {
            unhighlightItem()
        }
    }

    private fun callSelectedEvent(componentId: Int, title: String) {
        val args = SelectedComponentHistoryArgs(componentId, title)
        _selectedEvent.value = Event(args)
    }

    private fun unhighlightItem() {
        highlightedIdSaver.notifyChangedAndResetHighlightedId()
        callActionModeEvent()
    }

    private fun callActionModeEvent() {
        _actionModeEvent.value = highlightedIdSaver.isHighlightMode()
    }

    override fun actionModeClosed() {
        if(highlightedIdSaver.isHighlightMode()) {
            unhighlightItem()
        }
    }

    fun presentationLongClick(id: Int) {
        if (highlightedIdSaver.isNotHighlightMode()) {
            highlightItemAndSetActionMode(id)
        }
    }

    private fun highlightItemAndSetActionMode(id: Int) {
        highlightedIdSaver.saveHighlightedItemIdAndNotifyItChanged(id)
        callActionModeEvent()
    }

    override fun deleteHighlightedPresentation() {
        val id = highlightedIdSaver.highlightedId
        viewModelScope.launch {
            val history = dataSource.getHistoryById(id)
            history?.let{
                dataSource.deleteHistory(history)
            }
            unhighlightItem()
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