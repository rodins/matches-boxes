package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class MatchesBoxSetsListViewModel(private val radioComponentsDataSource: RadioComponentsDataSource): ViewModel() {
    private val _matchesBoxSets = MutableLiveData<List<MatchesBoxSet>>()
    val matchesBoxSets: LiveData<List<MatchesBoxSet>>
        get() = _matchesBoxSets

    val isNoMatchesBoxSetsTextVisible = Transformations.map(matchesBoxSets) {
        it.isEmpty()
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _selectItemEvent = MutableLiveData<Event<Int>>()
    val selectItemEvent: LiveData<Event<Int>>
        get() = _selectItemEvent

    fun start(bagId: Int) {
        viewModelScope.launch{
            _matchesBoxSets.value = radioComponentsDataSource.getMatchesBoxSetsByBagId(bagId)
        }
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }
}