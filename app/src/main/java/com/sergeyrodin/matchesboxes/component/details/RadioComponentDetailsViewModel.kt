package com.sergeyrodin.matchesboxes.component.details

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentDetails
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class RadioComponentDetailsViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val details = componentId.switchMap{ id ->
        liveData{
            emit(dataSource.getRadioComponentDetailsById(id))
        }
    }

    private val _editEvent = MutableLiveData<Event<RadioComponent>>()
    val editEvent: LiveData<Event<RadioComponent>>
        get() = _editEvent

    fun start(id: Int) {
        if(details.value == null)
            componentId.value = id
    }

    fun editComponent() {
        viewModelScope.launch {
            componentId.value?.let{
                val component = dataSource.getRadioComponentById(it)
                _editEvent.value = Event(component!!)
            }
        }
    }

}

class RadioComponentDetailsViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RadioComponentDetailsViewModel::class.java)) {
            return RadioComponentDetailsViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No ViewModel class found.")
        }
    }

}