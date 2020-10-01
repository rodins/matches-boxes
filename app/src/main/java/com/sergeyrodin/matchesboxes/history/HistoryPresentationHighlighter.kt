package com.sergeyrodin.matchesboxes.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET

class HistoryPresentationHighlighter {
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

    fun makePositionHighlighted(position: Int) {
        highlightedPosition = position
        _itemChangedEvent.value = Event(position)
    }

    fun makeHighlightedPositionNotHighlighted() {
        _itemChangedEvent.value = Event(highlightedPosition)
        highlightedPosition = NO_ID_SET
    }

    fun resetHighlightModeAfterDelete() {
        highlightedPosition = NO_ID_SET
    }
}