package com.sergeyrodin.matchesboxes.component.details

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentDetails
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class RadioComponentDetailsViewModel(dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val details = componentId.switchMap{ id ->
        liveData{
            emit(dataSource.getRadioComponentDetailsById(id))
        }
    }

    fun start(id: Int) {
        componentId.value = id
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