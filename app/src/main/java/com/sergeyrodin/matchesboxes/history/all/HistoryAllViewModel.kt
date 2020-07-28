package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class HistoryAllViewModel(dataSource: RadioComponentsDataSource): ViewModel() {
    val historyList = dataSource.getHistoryList()

    val noHistoryTextVisible = historyList.map{
        it.isEmpty()
    }
}