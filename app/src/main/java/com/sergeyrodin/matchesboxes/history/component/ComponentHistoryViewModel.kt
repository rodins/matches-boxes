package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class ComponentHistoryViewModel(val dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val historyList = componentId.switchMap { id ->
        liveData {
            emit(dataSource.getHistoryListByComponentId(id))
        }
    }

    val noItemsTextVisible = historyList.map { list ->
        list.isEmpty()
    }

    fun start(id: Int) {
        componentId.value = id
    }
}