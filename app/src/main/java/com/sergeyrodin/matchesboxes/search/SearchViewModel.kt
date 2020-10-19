package com.sergeyrodin.matchesboxes.search

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class SearchViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel() {
    private val searchQuery = MutableLiveData<String>()
    val items = searchQuery.switchMap{ query ->
        liveData {
            if (query.trim() != "") {
                emit(dataSource.getRadioComponentsByQuery(query))
            } else {
                emit(listOf())
            }
        }
    }

    val noComponentsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    fun start(query: String) {
        if(searchQuery.value != query)
            searchQuery.value = query
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }
}