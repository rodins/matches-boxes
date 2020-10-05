package com.sergeyrodin.matchesboxes.history.all

import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.history.HighligtedPositionSaverAndNotifier

class HistoryDeleter(
    private val dataSource: RadioComponentsDataSource,
    private val converter: HistoryPresentationConverter,
    private val highlightedPositionSaver: HighligtedPositionSaverAndNotifier
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
            converter.convert()
        }
    }
}