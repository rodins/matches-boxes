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


    val bagTitle = bagId.switchMap {
        liveData{
            val bag = radioComponentsDataSource.getBagById(it)
            emit(bag?.name)
        }
    }


    fun start(id: Int) {
        bagId.value = id
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }
}