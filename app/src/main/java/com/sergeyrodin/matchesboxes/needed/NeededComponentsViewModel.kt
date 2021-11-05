package com.sergeyrodin.matchesboxes.needed

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NeededComponentsViewModel @Inject constructor(
    dataSource: RadioComponentsDataSource): ViewModel()  {

    private val _selectedComponentEvent = MutableLiveData<Event<Int>>()
    val selectedComponentEvent: LiveData<Event<Int>>
        get() = _selectedComponentEvent

    val items = dataSource.getDisplayQuantityListToBuy()

    val noComponentsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }

    fun selectComponent(id: Int) {
        _selectedComponentEvent.value = Event(id)
    }
}
