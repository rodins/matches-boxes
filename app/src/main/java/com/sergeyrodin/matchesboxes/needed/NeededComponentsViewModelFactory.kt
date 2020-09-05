package com.sergeyrodin.matchesboxes.needed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class NeededComponentsViewModelFactory(private  val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NeededComponentsViewModel::class.java)) {
            return NeededComponentsViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("Unknown viewModel class")
        }
    }
}