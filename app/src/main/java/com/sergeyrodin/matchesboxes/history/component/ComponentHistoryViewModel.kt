package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.DeltaCalculator
import com.sergeyrodin.matchesboxes.history.HihgligtedPositionSaverAndNotifier
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import kotlinx.coroutines.launch

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val positionSaverAndNotifier = HihgligtedPositionSaverAndNotifier()
    private val converter = ConverterToComponentHistoryPresentation(dataSource)
    private val deleter = ComponentHistoryDeleter(dataSource, positionSaverAndNotifier, converter)

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
            val presentation = getPresentationByPosition(position)
            setPresentationHighlighted(presentation)
            positionSaverAndNotifier.saveHighlightedPositionAndNotifyItChanged(position)
            setActionDeleteVisible()
        }
    }

    private fun setActionDeleteVisible() {
        _actionDeleteVisibleEvent.value = Event(true)
    }

    private fun getPresentationByPosition(position: Int): ComponentHistoryPresentation? {
        return historyPresentationItems.value?.get(position)
    }

    private fun setPresentationHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = true
    }

    fun presentationClick() {
        if(positionSaverAndNotifier.isHighlightMode()) {
            val presentation = getHighlightedPresentation()
            makePresentationNotHighlighted(presentation)
            positionSaverAndNotifier.notifyChangedAndResetHighlightedPosition()
            setActionDeleteNotVisible()
        }
    }

    private fun getHighlightedPresentation(): ComponentHistoryPresentation? {
        return historyPresentationItems.value?.get(positionSaverAndNotifier.highlightedPosition)
    }

    private fun makePresentationNotHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = false
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