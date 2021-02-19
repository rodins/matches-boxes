package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HistoryActionModeModel
import com.sergeyrodin.matchesboxes.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryAllViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource) : ViewModel(),
    HistoryActionModeModel {

    private var deleteItemId = -1

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

    private val _highlightedPositionEvent = MutableLiveData<Int>()
    val highlightedPositionEvent: LiveData<Int>
        get() = _highlightedPositionEvent

    fun presentationClick(componentId: Int, name: String) {
        if (isNotHighlighted()) {
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
        val prevId = resetAndReturnHighlightedId()
        callHighlightedPositionEvent(prevId)
        callActionModeEvent()
    }

    private fun callActionModeEvent() {
        _actionModeEvent.value = isHighlighted()
    }

    init {
        resetAndReturnHighlightedId()
    }

    override fun actionModeClosed() {
        if(isHighlighted()) {
            unhighlightItem()
        }
    }

    fun presentationLongClick(id: Int) {
        if (isNotHighlighted()) {
            setDeleteItemId(id)
            setHighlightedId(id)
            callHighlightedPositionEvent(id)
            callActionModeEvent()
        }
    }

    private fun setDeleteItemId(id: Int) {
        deleteItemId = id
    }

    private fun callHighlightedPositionEvent(id: Int) {
        _highlightedPositionEvent.value = getPositionById(id)
    }

    private fun getPositionById(id: Int): Int {
        return historyItems.value?.indexOfFirst {
            it.id == id
        } ?: -1
    }

    override fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            val history = dataSource.getHistoryById(deleteItemId)
            history?.let{
                dataSource.deleteHistory(history)
            }
            resetDeleteItemId()
            unhighlightItem()
        }
    }

    private fun resetDeleteItemId() {
        deleteItemId = -1
    }
}

data class SelectedComponentHistoryArgs(
    var componentId: Int,
    var name: String
)