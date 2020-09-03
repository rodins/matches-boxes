package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class RadioComponentManipulatorViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RadioComponentManipulatorViewModel::class.java)) {
            return RadioComponentManipulatorViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}