package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class ComponentHistoryViewModel(val dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val historyList = componentId.switchMap { id ->
        liveData {
            emit(dataSource.getHistoryListByComponentId(id))
        }
    }

    fun start(id: Int) {
        componentId.value = id
    }
}