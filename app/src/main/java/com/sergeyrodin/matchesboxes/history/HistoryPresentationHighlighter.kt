package com.sergeyrodin.matchesboxes.history

class HistoryPresentationHighlighter(
    private val converter: HistoryConverter,
    private val highlightedPositionSaver: HighlightedPositionSaverAndNotifier
) {
    fun highlight(position: Int) {
        val presentation = getPresentationByPosition(position)
        setPresentationHighlighted(presentation)
    }

    private fun getPresentationByPosition(position: Int): HistoryPresentation? {
        return converter.historyPresentationItems.value?.get(position)
    }

    private fun setPresentationHighlighted(presentation: HistoryPresentation?) {
        presentation?.let {
            presentation.isHighlighted = true
        }
    }

    fun unhighlight() {
        val presentation = findHighlightedPresentation()
        setPresentationNotHighlighted(presentation)
    }

    private fun findHighlightedPresentation(): HistoryPresentation? {
        return getPresentationByPosition(highlightedPositionSaver.highlightedPosition)
    }

    private fun setPresentationNotHighlighted(presentation: HistoryPresentation?) {
        presentation?.isHighlighted = false
    }
}