package com.sergeyrodin.matchesboxes.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET

class HighlightedPositionSaverAndNotifier {
    var highlightedPosition: Int = NO_ID_SET
        private set
    private val _itemChangedEvent = MutableLiveData<Event<Int>>()
    val itemChangedEvent: LiveData<Event<Int>>
        get() = _itemChangedEvent

    fun isHighlightMode(): Boolean {
        return highlightedPosition != NO_ID_SET
    }

    fun isNotHighlightMode(): Boolean {
        return highlightedPosition == NO_ID_SET
    }

    fun saveHighlightedPositionAndNotifyItChanged(position: Int) {
        setHighlightedPosition(position)
        notifyItemChanged(position)
    }

    private fun setHighlightedPosition(position: Int) {
        highlightedPosition = position
    }

    private fun notifyItemChanged(position: Int) {
        _itemChangedEvent.value = Event(position)
    }

    fun notifyChangedAndResetHighlightedPosition() {
        notifyHighlightedItemChanged()
        resetHighlightedPosition()
    }

    private fun notifyHighlightedItemChanged() {
        _itemChangedEvent.value = Event(highlightedPosition)
    }

    private fun resetHighlightedPosition() {
        highlightedPosition = NO_ID_SET
    }

    fun resetHighlightedPositionAfterDelete() {
        resetHighlightedPosition()
    }
}