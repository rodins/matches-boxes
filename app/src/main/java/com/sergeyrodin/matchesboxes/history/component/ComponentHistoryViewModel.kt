package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString

class ComponentHistoryViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val componentId = MutableLiveData<Int>()
    val historyList = componentId.switchMap { id ->
        liveData {
            emit(getComponentHistoryPresentationListByComponentId(id))
        }
    }

    private suspend fun getComponentHistoryPresentationListByComponentId(id: Int): List<ComponentHistoryPresentation> {
        val list = dataSource.getHistoryListByComponentId(id)
        return list.map { history ->
            ComponentHistoryPresentation(
                history.id,
                convertLongToDateString(history.date),
                history.quantity.toString()
            )
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

data class ComponentHistoryPresentation(
    var id: Int,
    var date: String,
    var quantity: String
)