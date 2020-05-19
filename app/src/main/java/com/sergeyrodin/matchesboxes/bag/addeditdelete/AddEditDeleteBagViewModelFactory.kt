package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class AddEditDeleteBagViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditDeleteBagViewModel::class.java)) {
            return AddEditDeleteBagViewModel(
                dataSource
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}