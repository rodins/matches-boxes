package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class BagsListViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BagsListViewModel::class.java)) {
            return BagsListViewModel(
                dataSource
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}