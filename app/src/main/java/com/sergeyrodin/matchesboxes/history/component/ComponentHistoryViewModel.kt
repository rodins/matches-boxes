package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighlightedPositionSaverAndNotifier
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val positionSaverAndNotifier = HighlightedPositionSaverAndNotifier()
    private val converter = ConverterToComponentHistoryPresentation(dataSource)
    private val deleter = ComponentHistoryDeleter(dataSource, positionSaverAndNotifier, converter)
    private val highlighter = ComponentHistoryPresentationHighlighter(converter, positionSaverAndNotifier)

    val itemChangedEvent = positionSaverAndNotifier.itemChangedEvent
    val historyPresentationItems = converter.historyPresentationItems
    val noItemsTextVisible = historyPresentationItems.map { list ->
        list.isEmpty()
    }

    private val _actionDeleteVisibleEvent = MutableLiveData<Event<Boolean>>()
    val actionDeleteVisibleEvent: LiveData<Event<Boolean>>
        get() = _actionDeleteVisibleEvent

    fun start(id: Int) {
        converter.convert(id)
    }

    fun presentationLongClick(position: Int) {
        if(positionSaverAndNotifier.isNotHighlightMode()) {
            highlighter.highlight(position)
            positionSaverAndNotifier.saveHighlightedPositionAndNotifyItChanged(position)
            setActionDeleteVisible()
        }
    }

    private fun setActionDeleteVisible() {
        _actionDeleteVisibleEvent.value = Event(true)
    }

    fun presentationClick() {
        if(positionSaverAndNotifier.isHighlightMode()) {
            highlighter.unhighlight()
            positionSaverAndNotifier.notifyChangedAndResetHighlightedPosition()
            setActionDeleteNotVisible()
        }
    }

    private fun setActionDeleteNotVisible() {
        _actionDeleteVisibleEvent.value = Event(false)
    }

    fun deleteHighlightedPresentation() {
        viewModelScope.launch {
            deleter.deleteHighlightedPresentation()
            setActionDeleteNotVisible()
        }
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