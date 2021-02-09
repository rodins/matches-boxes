package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HistoryActionModeModel
import com.sergeyrodin.matchesboxes.util.*
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel(),
    HistoryActionModeModel {

    private val inputId = MutableLiveData<Int>()

    val historyItems: LiveData<List<History>> = inputId.switchMap { id ->
        dataSource.observeHistoryListByComponentId(id)
    }

    private val _highlightedPositionEvent = MutableLiveData<Int>()
    val highlightedPositionEvent: LiveData<Int>
        get() = _highlightedPositionEvent

    val noItemsTextVisible = historyItems.map { list ->
        calculateDeltasForHistoryItems(list)
        list.isEmpty()
    }

    private val _actionModeEvent = MutableLiveData<Boolean>()
    val actionModeEvent: LiveData<Boolean>
        get() = _actionModeEvent

    private var deleteItemId = -1

    init {
        resetAndReturnHighlightedId()
    }

    fun start(id: Int) {
        inputId.value = id
    }

    fun presentationLongClick(id: Int) {
        if(isNotHighlighted()) {
            setDeleteItemId(id)
            setHighlightedId(id)
            callHighlightedPositionEvent(id)
            activateActionMode()
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

    private fun activateActionMode() {
        _actionModeEvent.value = true
    }

    fun presentationClick() {
        if(isHighlighted()) {
            val prevId = resetAndReturnHighlightedId()
            callHighlightedPositionEvent(prevId)
            closeActionMode()
        }
    }

    private fun closeActionMode() {
        _actionModeEvent.value = false
    }

    override fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            val history = dataSource.getHistoryById(deleteItemId)
            history?.let{
                dataSource.deleteHistory(history)
            }
            resetDeleteItemId()
            resetAndReturnHighlightedId()
            closeActionMode()
        }
    }

    private fun resetDeleteItemId() {
        deleteItemId = -1
    }

    override fun actionModeClosed() {
        presentationClick()
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