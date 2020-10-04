package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.DeltaCalculator
import com.sergeyrodin.matchesboxes.util.convertLongToDateString

class ConverterToComponentHistoryPresentation(private val dataSource: RadioComponentsDataSource) {
    private val deltaCalculator = DeltaCalculator()
    private lateinit var historyItems: List<History>

    private val componentId = MutableLiveData<Int>()
    val historyPresentationItems = componentId.switchMap { id ->
        liveData {
            emit(getComponentHistoryPresentationListByComponentId(id))
        }
    }

    private suspend fun getComponentHistoryPresentationListByComponentId(id: Int): List<ComponentHistoryPresentation> {
        getHistoryItemsFromDb(id)
        deltaCalculator.calculateDeltasForHistoryItems(historyItems)
        return convertHistoryItemsToHistoryPresentationItems()
    }

    private suspend fun getHistoryItemsFromDb(id: Int) {
        historyItems = dataSource.getHistoryListByComponentId(id)
    }

    private fun convertHistoryItemsToHistoryPresentationItems(): List<ComponentHistoryPresentation> {
        return historyItems.map { history ->
            ComponentHistoryPresentation(
                history.id,
                convertLongToDateString(history.date),
                history.quantity.toString(),
                deltaCalculator.getDeltaByHistoryId(history.id)
            )
        }
    }

    fun convert(id: Int) {
        componentId.value = id
    }

    fun findHistoryById(id: Int?): History? {
        return historyItems.find {
            it.id == id
        }
    }
}