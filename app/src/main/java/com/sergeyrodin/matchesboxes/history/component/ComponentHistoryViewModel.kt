package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.DeltaCalculator
import com.sergeyrodin.matchesboxes.history.HighlightedItemIdSaverAndNotifier
import com.sergeyrodin.matchesboxes.history.HistoryActionModeModel
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel(),
    HistoryActionModeModel {
    private val highlightedIdSaver = HighlightedItemIdSaverAndNotifier()
    private val deltaCalculator = DeltaCalculator()

    private val inputId = MutableLiveData<Int>()

    val historyItems: LiveData<List<History>> = inputId.switchMap { id ->
        dataSource.observeHistoryListByComponentId(id)
    }

    val deltas = historyItems.map {
        deltaCalculator.calculateDeltasForHistoryItems(it)
    }

    val highlightedItemIdEvent = highlightedIdSaver.highlightedItemIdEvent

    val noItemsTextVisible = historyItems.map { list ->
        list.isEmpty()
    }

    private val _actionModeEvent = MutableLiveData<Boolean>()
    val actionModeEvent: LiveData<Boolean>
        get() = _actionModeEvent

    fun start(id: Int) {
        inputId.value = id
    }

    fun presentationLongClick(id: Int) {
        if(highlightedIdSaver.isNotHighlightMode()) {
            highlightedIdSaver.saveHighlightedItemIdAndNotifyItChanged(id)
            activateActionMode()
        }
    }

    private fun activateActionMode() {
        _actionModeEvent.value = true
    }

    fun presentationClick() {
        if(highlightedIdSaver.isHighlightMode()) {
            highlightedIdSaver.notifyChangedAndResetHighlightedId()
            closeActionMode()
        }
    }

    private fun closeActionMode() {
        _actionModeEvent.value = false
    }

    override fun deleteHighlightedPresentation() {
        val id = highlightedIdSaver.highlightedId
        viewModelScope.launch {
            val history = dataSource.getHistoryById(id)
            history?.let{
                dataSource.deleteHistory(history)
            }
            closeActionMode()
        }
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