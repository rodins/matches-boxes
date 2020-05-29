package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class MatchesBoxListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _matchesBoxes = MutableLiveData<List<MatchesBox>>()
    val matchesBoxes: LiveData<List<MatchesBox>>
        get() = _matchesBoxes

    val isNoItemsTextVisible = Transformations.map(matchesBoxes) {
        it.isEmpty()
    }

    private val _addMatchesBoxEvent = MutableLiveData<Event<Unit>>()
    val addMatchesBoxEvent: LiveData<Event<Unit>>
        get() = _addMatchesBoxEvent

    private val _selectMatchesBoxEvent = MutableLiveData<Event<Int>>()
    val selectMatchesBoxEvent: LiveData<Event<Int>>
        get() = _selectMatchesBoxEvent

    fun start(setId: Int) {
        viewModelScope.launch {
            _matchesBoxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        }
    }

    fun addMatchesBox() {
        _addMatchesBoxEvent.value = Event(Unit)
    }

    fun selectMatchesBox(id: Int) {
        _selectMatchesBoxEvent.value = Event(id)
    }

}