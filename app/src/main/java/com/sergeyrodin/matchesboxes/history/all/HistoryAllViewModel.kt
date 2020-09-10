package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import java.lang.IllegalArgumentException

class HistoryAllViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val historyList = dataSource.getHistoryList()

    val displayHistoryList = historyList.switchMap{ list ->
        liveData{
            emit(convertHistoryListToHistoryPresentationList(list))
        }
    }

    private suspend fun convertHistoryListToHistoryPresentationList(
        list: List<History>
    ): List<HistoryPresentation> {
        val outputList = mutableListOf<HistoryPresentation>()
        list.forEach { history ->
            outputList.add(convertHistoryToHistoryPresentation(history))
        }
        return outputList
    }

    private suspend fun convertHistoryToHistoryPresentation(history: History): HistoryPresentation {
        val component = dataSource.getRadioComponentById(history.componentId)
        return HistoryPresentation(
            history.id,
            history.componentId,
            component?.name ?: "",
            history.quantity.toString(),
            convertLongToDateString(history.date)
        )
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