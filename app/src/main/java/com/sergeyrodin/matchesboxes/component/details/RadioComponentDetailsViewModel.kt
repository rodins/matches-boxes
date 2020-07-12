package com.sergeyrodin.matchesboxes.component.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sergeyrodin.matchesboxes.data.RadioComponentDetails
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class RadioComponentDetailsViewModel(dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val details = componentId.switchMap{ id ->
        liveData<RadioComponentDetails>{
            emit(dataSource.getRadioComponentDetailsById(id))
        }
    }

    fun start(id: Int) {
        componentId.value = id
    }

}