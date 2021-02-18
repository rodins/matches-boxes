package com.sergeyrodin.matchesboxes.search

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource) : ViewModel() {

    private val searchQuery = MutableLiveData<String>()
    val items = searchQuery.switchMap{ query ->
        liveData {
            if (query.trim() != "") {
                emit(dataSource.getRadioComponentsByQuery(query))
            } else {
                emit(listOf<RadioComponent>())
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
        if(searchQuery.value != query) {
            searchQuery.value = query
        }
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }
}