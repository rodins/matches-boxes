package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class MatchBoxSetManipulatorViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchesBoxSetManipulatorViewModel::class.java)) {
            return MatchesBoxSetManipulatorViewModel(
                dataSource
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}