package com.sergeyrodin.matchesboxes.needed

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class NeededComponentsViewModel(private val dataSource: RadioComponentsDataSource): ViewModel()  {
    val items = liveData {
        emit(dataSource.getRadioComponentsToBuy())
    }

    val noComponentsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }
}