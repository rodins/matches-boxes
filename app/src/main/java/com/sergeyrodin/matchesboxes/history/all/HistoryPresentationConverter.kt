package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.convertLongToDateString

class HistoryPresentationConverter(private val dataSource: RadioComponentsDataSource) {
    private val deltaCalculator = DeltaCalculator()
    private lateinit var historyItems: List<History>

    private val _historyPresentationItems = MutableLiveData<List<HistoryPresentation>>()
    val historyPresentationItems: LiveData<List<HistoryPresentation>>
        get() = _historyPresentationItems

    private val componentNames = mutableMapOf<Int, String>()

    suspend fun convert() {
        getHistoryItemsFromDb()
        deltaCalculator.calculateDeltasForHistoryItems(historyItems)
        convertHistoryItemsToHistoryPresentationItems()
    }

    private suspend fun getHistoryItemsFromDb() {
        historyItems = dataSource.getHistoryList()
    }

    private suspend fun convertHistoryItemsToHistoryPresentationItems() {
        val presentationItems = historyItems.map { history ->
            HistoryPresentation(
                history.id,
                history.componentId,
                getComponentName(history),
                history.quantity.toString(),
                convertLongToDateString(history.date),
                deltaCalculator.getDeltaByHistoryId(history.id)
            )
        }
        updatePresentationItems(presentationItems)
    }

    private suspend fun getComponentName(history: History): String {
        return getComponentNameFromCache(history) ?: getComponentNameFromDatabase(history)
    }

    private fun getComponentNameFromCache(history: History) =
        componentNames[history.componentId]

    private suspend fun getComponentNameFromDatabase(history: History): String {
        val component = dataSource.getRadioComponentById(history.componentId)
        val name = component?.name ?: ""
        componentNames[history.componentId] = name
        return name
    }

    private fun updatePresentationItems(presentationItems: List<HistoryPresentation>) {
        _historyPresentationItems.value = presentationItems
    }

    fun findHistoryById(id: Int?) : History?{
        return historyItems.find { history ->
            history.id == id
        }
    }
}