package com.sergeyrodin.matchesboxes.searchbuy.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class SearchBuyListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _items = MutableLiveData<List<RadioComponent>>()
    val items: LiveData<List<RadioComponent>>
        get() = _items

    val noComponentsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    fun start(query: String, isSearch: Boolean) {
        if(isSearch) {
            if(query.trim() != "") {
                viewModelScope.launch{
                    _items.value = dataSource.getRadioComponentsByQuery(query)
                }
            }
        }
    }
}