package com.sergeyrodin.matchesboxes.history

import androidx.lifecycle.LiveData
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.data.History

interface HistoryConverter {
    val historyPresentationItems: LiveData<List<HistoryPresentation>>
    suspend fun convert(id: Int = NO_ID_SET)
    fun findHistoryById(id: Int?): History?
}