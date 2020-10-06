package com.sergeyrodin.matchesboxes.history.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.DeltaCalculator
import com.sergeyrodin.matchesboxes.history.HistoryConverter
import com.sergeyrodin.matchesboxes.history.HistoryPresentation
import com.sergeyrodin.matchesboxes.util.convertLongToDateString

class ConverterToComponentHistoryPresentation(private val dataSource: RadioComponentsDataSource) :
    HistoryConverter {
    private val deltaCalculator = DeltaCalculator()
    private lateinit var historyItems: List<History>

    private val _historyPresentationItems = MutableLiveData<List<HistoryPresentation>>()
    override val historyPresentationItems: LiveData<List<HistoryPresentation>>
        get() = _historyPresentationItems

    private suspend fun getComponentHistoryPresentationListByComponentId(id: Int): List<HistoryPresentation> {
        getHistoryItemsFromDb(id)
        deltaCalculator.calculateDeltasForHistoryItems(historyItems)
        return convertHistoryItemsToHistoryPresentationItems()
    }

    private suspend fun getHistoryItemsFromDb(id: Int) {
        historyItems = dataSource.getHistoryListByComponentId(id)
    }

    private fun convertHistoryItemsToHistoryPresentationItems(): List<HistoryPresentation> {
        return historyItems.map { history ->
            HistoryPresentation(
                history.id,
                convertLongToDateString(history.date),
                history.quantity.toString(),
                delta = deltaCalculator.getDeltaByHistoryId(history.id)
            )
        }
    }

    override suspend fun convert(id: Int) {
        _historyPresentationItems.value = getComponentHistoryPresentationListByComponentId(id)
    }

    override fun findHistoryById(id: Int?): History? {
        return historyItems.find {
            it.id == id
        }
    }
}