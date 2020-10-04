package com.sergeyrodin.matchesboxes.history

import com.sergeyrodin.matchesboxes.data.History

class DeltaCalculator {
    private val previousHistoryQuantity = mutableMapOf<Int,Int>()
    private val deltas = mutableMapOf<Int, String>()

    fun calculateDeltasForHistoryItems(historyItems: List<History>) {
        historyItems.reversed().forEach { history ->
            val delta = getHistoryDelta(history)
            deltas[history.id] = delta
        }
    }

    private fun getHistoryDelta(history: History): String {
        val previousQuantity = previousHistoryQuantity[history.componentId]
        previousHistoryQuantity[history.componentId] = history.quantity
        if (previousQuantity != null) {
            return calculateHistoryDelta(history, previousQuantity)
        }
        return ""
    }

    private fun calculateHistoryDelta(
        history: History,
        previousQuantity: Int
    ): String {
        val numericDelta = history.quantity - previousQuantity
        if (numericDelta > 0) {
            return "+$numericDelta"
        }
        if(numericDelta < 0){
            return numericDelta.toString()
        }
        return ""
    }

    fun getDeltaByHistoryId(id: Int) = deltas[id] ?: ""
}