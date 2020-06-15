package com.sergeyrodin.matchesboxes.searchbuy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class SearchBuyViewModelFactory(private  val dataSource: RadioComponentsDataSource):ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchBuyViewModel::class.java)) {
            return SearchBuyViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("Unknown viewModel class")
        }
    }

}