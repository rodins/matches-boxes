package com.sergeyrodin.matchesboxes.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HighlightedItemIdSaverAndNotifier {
    var highlightedId: Int = -1
        private set
    private val _itemChangedEvent = MutableLiveData<Int>()
    val highlightedItemIdEvent: LiveData<Int>
        get() = _itemChangedEvent

    fun isHighlightMode(): Boolean {
        return highlightedId != -1
    }

    fun isNotHighlightMode(): Boolean {
        return highlightedId == -1
    }

    fun saveHighlightedItemIdAndNotifyItChanged(id: Int) {
        setHighlightedId(id)
        notifyItemChanged(id)
    }

    private fun setHighlightedId(id: Int) {
        highlightedId = id
    }

    private fun notifyItemChanged(id: Int) {
        _itemChangedEvent.value = id
    }

    fun notifyChangedAndResetHighlightedId() {
        resetHighlightedId()
        notifyHighlightedIdChanged()
    }

    private fun notifyHighlightedIdChanged() {
        _itemChangedEvent.value = highlightedId
    }

    private fun resetHighlightedId() {
        highlightedId = -1
    }
}