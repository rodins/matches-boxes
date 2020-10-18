package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighlightedPositionSaverAndNotifier
import com.sergeyrodin.matchesboxes.history.HistoryActionModeModel
import com.sergeyrodin.matchesboxes.history.HistoryDeleter
import com.sergeyrodin.matchesboxes.history.HistoryPresentationHighlighter
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(dataSource: RadioComponentsDataSource): ViewModel(),
    HistoryActionModeModel {
    private val positionSaverAndNotifier = HighlightedPositionSaverAndNotifier()
    private val converter = ConverterToComponentHistoryPresentation(dataSource)
    private val deleter = HistoryDeleter(dataSource, converter, positionSaverAndNotifier)
    private val highlighter = HistoryPresentationHighlighter(converter, positionSaverAndNotifier)

    val itemChangedEvent = positionSaverAndNotifier.itemChangedEvent
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

    fun presentationLongClick(position: Int) {
        if(positionSaverAndNotifier.isNotHighlightMode()) {
            highlighter.highlight(position)
            positionSaverAndNotifier.saveHighlightedPositionAndNotifyItChanged(position)
            activateActionMode()
        }
    }

    private fun activateActionMode() {
        _actionModeEvent.value = true
    }

    fun presentationClick() {
        if(positionSaverAndNotifier.isHighlightMode()) {
            highlighter.unhighlight()
            positionSaverAndNotifier.notifyChangedAndResetHighlightedPosition()
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