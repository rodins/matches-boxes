package com.sergeyrodin.matchesboxes.needed

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NeededComponentsViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource): ViewModel()  {
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