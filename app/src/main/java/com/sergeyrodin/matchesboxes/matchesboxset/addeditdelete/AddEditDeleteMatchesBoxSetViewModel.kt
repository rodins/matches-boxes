package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteMatchesBoxSetViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    private var matchesBoxSet: MatchesBoxSet? = null
    private var _bagId: Int = 0

    private val _addedEvent = MutableLiveData<Event<Unit>>()
    val addedEvent: LiveData<Event<Unit>>
        get() = _addedEvent

    private val _updatedEvent = MutableLiveData<Event<MatchesBoxSet>>()
    val updatedEvent: LiveData<Event<MatchesBoxSet>>
        get() = _updatedEvent

    private val _deletedEvent = MutableLiveData<Event<Unit>>()
    val deletedEvent: LiveData<Event<Unit>>
        get() = _deletedEvent

    val name = MutableLiveData<String>()

    fun start(bagId: Int, setId: Int) {
        _bagId = bagId
        viewModelScope.launch {
            matchesBoxSet = dataSource.getMatchesBoxSetById(setId)
            name.value = matchesBoxSet?.name?:""
        }
    }

    fun saveMatchesBoxSet() {
        if(name.value?.trim() != "") {
            if(matchesBoxSet == null) {
                addMatchesBoxSet()
            }else{
                updateMatchesBoxSet()
            }
        }
    }

    fun deleteMatchesBoxSet() {
        viewModelScope.launch {
            dataSource.deleteMatchesBoxSet(matchesBoxSet!!)
            _deletedEvent.value = Event(Unit)
        }
    }

    private fun addMatchesBoxSet() {
        viewModelScope.launch {
            val newMatchesBoxSet = MatchesBoxSet(name = name.value!!, bagId = _bagId)
            dataSource.insertMatchesBoxSet(newMatchesBoxSet)
            _addedEvent.value = Event(Unit)
        }
    }

    private fun updateMatchesBoxSet() {
        viewModelScope.launch {
            matchesBoxSet?.name = name.value!!
            dataSource.updateMatchesBoxSet(matchesBoxSet!!)
            _updatedEvent.value = Event(matchesBoxSet!!)
        }
    }
}