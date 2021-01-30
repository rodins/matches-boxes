package com.sergeyrodin.matchesboxes.history

import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class HistoryDeleter(
    private val dataSource: RadioComponentsDataSource,
    private val converter: HistoryConverter,
    private val highlightedItemIdSaver: HighlightedItemIdSaverAndNotifier
    ) {
    suspend fun deleteHighlightedPresentation() {
            val history = findHistoryByHighlightedPresentationId()
            deleteHistory(history)
            highlightedItemIdSaver.resetHighlightedIdAfterDelete()
    }

    private fun findHistoryByHighlightedPresentationId(): History? {
        val highlightedPresentation = findHighlightedPresentation()
        return converter.findHistoryById(highlightedPresentation?.id)
    }

    private fun findHighlightedPresentation(): HistoryPresentation? {
        return getPresentationById(highlightedItemIdSaver.highlightedId)
    }

    private fun getPresentationById(id: Int): HistoryPresentation? {
        return converter.historyPresentationItems.value?.find {
            it.id == id
        }
    }

    private suspend fun deleteHistory(history: History?) {
        history?.let {
            dataSource.deleteHistory(history)
            converter.convert(history.componentId)
        }
    }
}