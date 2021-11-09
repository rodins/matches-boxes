package com.sergeyrodin.matchesboxes.search

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.QuantityItemModel
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource) : ViewModel() {

    private val searchQuery = MutableLiveData<String>()
    val items = searchQuery.switchMap { query ->
        liveData {
            if (query != "") {
                emit(dataSource.getDisplayQuantityListByQuery(query))
            } else {
                emit(listOf<QuantityItemModel>())
            }
        }
    }

    private val _selectedComponentEvent = MutableLiveData<Event<Int>>()
    val selectedComponentEvent: LiveData<Event<Int>>
        get() = _selectedComponentEvent

    val noComponentsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    fun start(query: String) {
        if(searchQuery.value != query) {
            searchQuery.value = query
        }
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }

    fun selectComponent(id: Int) {
        _selectedComponentEvent.value = Event(id)
    }
}