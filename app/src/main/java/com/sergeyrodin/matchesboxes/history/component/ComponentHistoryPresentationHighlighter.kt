package com.sergeyrodin.matchesboxes.history.component

import com.sergeyrodin.matchesboxes.history.HighlightedPositionSaverAndNotifier

class ComponentHistoryPresentationHighlighter(
    private val converter: ConverterToComponentHistoryPresentation,
    private val positionSaverAndNotifier: HighlightedPositionSaverAndNotifier) {

    fun highlight(position: Int) {
        val presentation = getPresentationByPosition(position)
        setPresentationHighlighted(presentation)
    }

    private fun getPresentationByPosition(position: Int): ComponentHistoryPresentation? {
        return converter.historyPresentationItems.value?.get(position)
    }

    private fun setPresentationHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = true
    }

    fun unhighlight() {
        val presentation = getHighlightedPresentation()
        makePresentationNotHighlighted(presentation)
    }

    private fun getHighlightedPresentation(): ComponentHistoryPresentation? {
        return converter.historyPresentationItems.value?.get(positionSaverAndNotifier.highlightedPosition)
    }

    private fun makePresentationNotHighlighted(presentation: ComponentHistoryPresentation?) {
        presentation?.isHighlighted = false
    }
}