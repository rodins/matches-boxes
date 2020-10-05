package com.sergeyrodin.matchesboxes.history.component

import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighligtedPositionSaverAndNotifier

class ComponentHistoryDeleter(
    private val dataSource: RadioComponentsDataSource,
    private val positionSaverAndNotifier: HighligtedPositionSaverAndNotifier,
    private val converter: ConverterToComponentHistoryPresentation
) {
    suspend fun deleteHighlightedPresentation() {
        val presentation = getHighlightedPresentation()
        val history = getHighlightedHistoryItem(presentation)
        deleteHistory(history)
    }

    private fun getHighlightedPresentation(): ComponentHistoryPresentation? {
        return converter.historyPresentationItems.value?.get(positionSaverAndNotifier.highlightedPosition)
    }

    private fun getHighlightedHistoryItem(presentation: ComponentHistoryPresentation?): History? {
        return converter.findHistoryById(presentation?.id)
    }

    private suspend fun deleteHistory(history: History?) {
        history?.let {
            dataSource.deleteHistory(history)
            refreshHistoryItemsFromDb(history)
            positionSaverAndNotifier.resetHighlightedPositionAfterDelete()
        }
    }

    private fun refreshHistoryItemsFromDb(history: History) {
        converter.convert(history.componentId)
    }
}