package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import java.lang.IllegalArgumentException

class MatchesBoxListViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MatchesBoxListViewModel::class.java)) {
            return MatchesBoxListViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}