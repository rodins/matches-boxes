package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import java.lang.IllegalArgumentException

class HistoryAllViewModel(dataSource: RadioComponentsDataSource): ViewModel() {
    private val historyList = dataSource.getHistoryList()

    val displayHistoryList = historyList.switchMap{list ->
        liveData{
            val outputList = mutableListOf<HistoryPresentation>()
            list.forEach { history ->
                val component = dataSource.getRadioComponentById(history.componentId)
                val displayHistory = HistoryPresentation(
                    history.id,
                    history.componentId,
                    component?.name?:"",
                    history.quantity.toString(),
                    convertLongToDateString(history.date)
                )
                outputList.add(displayHistory)
            }
            emit(outputList)
        }
    }

    val noHistoryTextVisible = historyList.map{
        it.isEmpty()
    }
}

data class HistoryPresentation(
    var id: Int,
    var componentId: Int,
    var name: String,
    var quantity: String,
    var date: String
)

class HistoryAllViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryAllViewModel::class.java)) {
            return HistoryAllViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No view mode class found.")
        }
    }
}