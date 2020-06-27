package com.sergeyrodin.matchesboxes.component.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class RadioComponentsListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val boxId = MutableLiveData<Int>()
    val items = boxId.switchMap{
        liveData{
            emit(dataSource.getRadioComponentsByMatchesBoxId(it))
        }
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    val noItemsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    val boxTitle = boxId.switchMap {
        liveData{
            val box = dataSource.getMatchesBoxById(it)
            emit(box?.name)
        }
    }

    fun start(id: Int) {
        boxId.value = id
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }
}