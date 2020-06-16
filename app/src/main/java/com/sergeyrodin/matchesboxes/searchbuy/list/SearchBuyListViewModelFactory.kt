package com.sergeyrodin.matchesboxes.searchbuy.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class SearchBuyListViewModelFactory(private  val dataSource: RadioComponentsDataSource):ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchBuyListViewModel::class.java)) {
            return SearchBuyListViewModel(
                dataSource
            ) as T
        }else {
            throw IllegalArgumentException("Unknown viewModel class")
        }
    }

}