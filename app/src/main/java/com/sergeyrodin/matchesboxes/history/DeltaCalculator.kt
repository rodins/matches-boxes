package com.sergeyrodin.matchesboxes.history

import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.HistoryModel

class DeltaCalculator {
    private val previousHistoryQuantity = mutableMapOf<Int,Int>()
    private val deltas = mutableMapOf<Int, String>()

    fun calculateDeltasForHistoryItems(historyItems: List<History>) : Map<Int, String>{
        historyItems.reversed().forEach { history ->
            val delta = getHistoryDelta(history)
            deltas[history.id] = delta
        }
        return deltas
    }

    private fun getHistoryDelta(history: History): String {
        val previousQuantity = previousHistoryQuantity[history.componentId]
        previousHistoryQuantity[history.componentId] = history.quantity
        if (previousQuantity != null) {
            return calculateHistoryDelta(history, previousQuantity)
        }
        return ""
    }

    fun calculateDeltasForHistoryModelItems(historyItems: List<HistoryModel>): Map<Int, String> {
        historyItems.reversed().forEach { historyModel ->
            val delta = getHistoryDelta(historyModel)
            deltas[historyModel.id] = delta
        }
        return deltas
    }

    private fun getHistoryDelta(history: HistoryModel): String {
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

    private fun calculateHistoryDelta(
        history: HistoryModel,
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