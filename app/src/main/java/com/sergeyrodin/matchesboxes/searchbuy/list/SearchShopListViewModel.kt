package com.sergeyrodin.matchesboxes.searchbuy.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class SearchShopListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
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

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    fun start(query: String, searchMode: Boolean) {
        searchQuery = query
        isSearch.value = searchMode
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }
}