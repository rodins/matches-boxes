package com.sergeyrodin.matchesboxes.searchbuy.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class SearchBuyListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private lateinit var searchQuery: String
    private val isSearch = MutableLiveData<Boolean>()
    val items = isSearch.switchMap {
        liveData{
            if(it) {
                if(searchQuery.trim() != ""){
                    emit(dataSource.getRadioComponentsByQuery(searchQuery))
                }else {
                    emit(listOf())
                }
            }else {
                emit(dataSource.getRadioComponentsToBuy())
            }
        }
    }

    val noComponentsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    fun start(query: String, searchMode: Boolean) {
        searchQuery = query
        isSearch.value = searchMode
    }
}