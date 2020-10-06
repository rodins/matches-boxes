package com.sergeyrodin.matchesboxes.history

import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class HistoryDeleter(
    private val dataSource: RadioComponentsDataSource,
    private val converter: HistoryConverter,
    private val highlightedPositionSaver: HighlightedPositionSaverAndNotifier
    ) {
    suspend fun deleteHighlightedPresentation() {
            val history = findHistoryByHighlightedPresentationId()
            deleteHistory(history)
            highlightedPositionSaver.resetHighlightedPositionAfterDelete()
    }

    private fun findHistoryByHighlightedPresentationId(): History? {
        val highlightedPresentation = findHighlightedPresentation()
        return converter.findHistoryById(highlightedPresentation?.id)
    }

    private fun findHighlightedPresentation(): HistoryPresentation? {
        return getPresentationByPosition(highlightedPositionSaver.highlightedPosition)
    }

    private fun getPresentationByPosition(position: Int): HistoryPresentation? {
        return converter.historyPresentationItems.value?.get(position)
    }

    private suspend fun deleteHistory(history: History?) {
        history?.let {
            dataSource.deleteHistory(history)
            converter.convert(history.componentId)
        }
    }
}