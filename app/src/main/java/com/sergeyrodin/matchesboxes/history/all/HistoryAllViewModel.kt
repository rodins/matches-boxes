package com.sergeyrodin.matchesboxes.history.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import java.lang.IllegalArgumentException

class HistoryAllViewModel(dataSource: RadioComponentsDataSource): ViewModel() {
    val historyList = dataSource.getHistoryList()

    val noHistoryTextVisible = historyList.map{
        it.isEmpty()
    }
}

class HistoryAddViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryAllViewModel::class.java)) {
            return HistoryAllViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No view mode class found.")
        }
    }
}