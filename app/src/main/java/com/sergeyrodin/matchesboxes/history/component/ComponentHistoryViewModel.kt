package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighlightedItemIdSaverAndNotifier
import com.sergeyrodin.matchesboxes.history.HistoryActionModeModel
import com.sergeyrodin.matchesboxes.history.HistoryDeleter
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(dataSource: RadioComponentsDataSource): ViewModel(),
    HistoryActionModeModel {
    private val itemIdSaverAndNotifier = HighlightedItemIdSaverAndNotifier()
    private val converter = ConverterToComponentHistoryPresentation(dataSource)
    private val deleter = HistoryDeleter(dataSource, converter, itemIdSaverAndNotifier)

    val highlightedItemIdEvent = itemIdSaverAndNotifier.highlightedItemIdEvent
    val historyPresentationItems = converter.historyPresentationItems
    val noItemsTextVisible = historyPresentationItems.map { list ->
        list.isEmpty()
    }

    private val _actionModeEvent = MutableLiveData<Boolean>()
    val actionModeEvent: LiveData<Boolean>
        get() = _actionModeEvent

    fun start(id: Int) {
        viewModelScope.launch {
            if(historyPresentationItems.value == null)
                converter.convert(id)
        }
    }

    fun presentationLongClick(id: Int) {
        if(itemIdSaverAndNotifier.isNotHighlightMode()) {
            itemIdSaverAndNotifier.saveHighlightedItemIdAndNotifyItChanged(id)
            activateActionMode()
        }
    }

    private fun activateActionMode() {
        _actionModeEvent.value = true
    }

    fun presentationClick() {
        if(itemIdSaverAndNotifier.isHighlightMode()) {
            itemIdSaverAndNotifier.notifyChangedAndResetHighlightedId()
            closeActionMode()
        }
    }

    private fun closeActionMode() {
        _actionModeEvent.value = false
    }

    override fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            deleter.deleteHighlightedPresentation()
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