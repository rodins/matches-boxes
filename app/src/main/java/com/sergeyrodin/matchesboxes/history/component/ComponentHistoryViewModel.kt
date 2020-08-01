package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString

class ComponentHistoryViewModel(val dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val historyList = componentId.switchMap { id ->
        liveData {
            val list = dataSource.getHistoryListByComponentId(id)
            val output = list.map{history ->
                DisplayComponentHistory(
                    history.id,
                    convertLongToDateString(history.date),
                    history.quantity.toString()
                )
            }
            emit(output)
        }
    }

    val noItemsTextVisible = historyList.map { list ->
        list.isEmpty()
    }

    fun start(id: Int) {
        componentId.value = id
    }
}

class ComponentHistoryViewModelFactory(private var dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ComponentHistoryViewModel::class.java)) {
            return ComponentHistoryViewModel(dataSource) as T
        }else{
            throw IllegalArgumentException("No ViewModel class found.")
        }
    }
}

data class DisplayComponentHistory(
    var id: Int,
    var date: String,
    var quantity: String
)