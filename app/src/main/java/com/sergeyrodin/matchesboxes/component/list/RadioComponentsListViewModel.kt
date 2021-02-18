package com.sergeyrodin.matchesboxes.component.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RadioComponentsListViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val boxId = MutableLiveData<Int>()
    val componentsList = boxId.switchMap{
        liveData{
            emit(dataSource.getRadioComponentsByMatchesBoxId(it))
        }
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    val noComponentsTextVisible = Transformations.map(componentsList) { list ->
        list.isEmpty()
    }

    fun startComponent(id: Int) {
        if(componentsList.value == null)
            boxId.value = id
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }
}
