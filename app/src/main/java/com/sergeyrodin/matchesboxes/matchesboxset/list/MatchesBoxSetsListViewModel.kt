package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DisplayQuantity
import com.sergeyrodin.matchesboxes.util.getMatchesBoxSetQuantityList
import kotlinx.coroutines.launch

class MatchesBoxSetsListViewModel(private val radioComponentsDataSource: RadioComponentsDataSource): ViewModel() {
    private val bagId = MutableLiveData<Int>()
    val matchesBoxSets = bagId.switchMap{
        liveData{
            val items = radioComponentsDataSource.getMatchesBoxSetsByBagId(it)
            emit(getMatchesBoxSetQuantityList(radioComponentsDataSource, items))
        }
    }

    val isNoMatchesBoxSetsTextVisible = Transformations.map(matchesBoxSets) {
        it.isEmpty()
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _selectItemEvent = MutableLiveData<Event<Int>>()
    val selectItemEvent: LiveData<Event<Int>>
        get() = _selectItemEvent

    private val _bagTitle = MutableLiveData<String>()
    val bagTitle: LiveData<String>
        get() = _bagTitle

    fun start(id: Int) {
        bagId.value = id
        viewModelScope.launch{
            val bag = radioComponentsDataSource.getBagById(id)
            _bagTitle.value = bag?.name
        }
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }
}