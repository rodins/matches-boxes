package com.sergeyrodin.matchesboxes.history

import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.HistoryModel

class DeltaCalculator {
    private val previousHistoryQuantity = mutableMapOf<Int, Int>()
    private val deltas = mutableMapOf<Int, String>()

    fun calculateDeltasForHistoryItems(historyItems: List<History>): Map<Int, String> {
        val deltaItems = historyItems.map {
            DeltaItem(it.id, it.componentId, it.quantity)
        }
        return calculateDeltasForDeltaItems(deltaItems)
    }

    fun calculateDeltasForHistoryModelItems(historyItems: List<HistoryModel>): Map<Int, String> {
        val deltaItems = historyItems.map {
            DeltaItem(it.id, it.componentId, it.quantity)
        }
        return calculateDeltasForDeltaItems(deltaItems)
    }

    private fun calculateDeltasForDeltaItems(
        deltaItems: List<DeltaItem>
    ): Map<Int, String> {
        previousHistoryQuantity.clear()
        deltaItems.reversed().forEach { deltaItem ->
            val delta = getHistoryDelta(deltaItem)
            deltas[deltaItem.id] = delta
        }
        return deltas
    }

    private fun getHistoryDelta(deltaItem: DeltaItem): String {
        val previousQuantity = previousHistoryQuantity[deltaItem.componentId]
        previousHistoryQuantity[deltaItem.componentId] = deltaItem.quantity
        if (previousQuantity != null) {
            return calculateHistoryDelta(deltaItem.quantity, previousQuantity)
        }
        return ""
    }

    private fun calculateHistoryDelta(
        quantity: Int,
        previousQuantity: Int
    ): String {
        val numericDelta = quantity - previousQuantity
        if (numericDelta > 0) {
            return "+$numericDelta"
        }
        if (numericDelta < 0) {
            return numericDelta.toString()
        }
        return ""
    }
}

data class DeltaItem(
    val id: Int,
    val componentId: Int,
    val quantity: Int
)