package com.sergeyrodin.matchesboxes.searchbuy.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class SearchBuyEditViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory  {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchBuyEditViewModel::class.java)) {
            return SearchBuyEditViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}