package com.sergeyrodin.matchesboxes.component.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class RadioComponentsListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _items = MutableLiveData<List<RadioComponent>>()
    val items: LiveData<List<RadioComponent>>
        get() = _items

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    val noItemsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    private val _boxTitle = MutableLiveData<String>()
    val boxTitle: LiveData<String>
        get() = _boxTitle

    fun start(boxId: Int) {
        viewModelScope.launch {
            _items.value = dataSource.getRadioComponentsByMatchesBoxId(boxId)
            val box = dataSource.getMatchesBoxById(boxId)
            _boxTitle.value = box?.name
        }
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }
}