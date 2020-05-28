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

    private val _updatedEvent = MutableLiveData<Event<Unit>>()
    val updatedEvent: LiveData<Event<Unit>>
        get() = _updatedEvent

    private val _deletedEvent = MutableLiveData<Event<Unit>>()
    val deletedEvent: LiveData<Event<Unit>>
        get() = _deletedEvent

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name


    fun start(bagId: Int, setId: Int) {
        _bagId = bagId
        viewModelScope.launch {
            matchesBoxSet = dataSource.getMatchesBoxSetById(setId)
            _name.value = matchesBoxSet?.name?:""
        }
    }

    fun saveMatchesBoxSet(name: String) {
        if(name.trim() != "") {
            if(matchesBoxSet == null) {
                addMatchesBoxSet(name)
                _addedEvent.value = Event(Unit)
            }else{
                updateMatchesBoxSet(name)
                _updatedEvent.value = Event(Unit)
            }
        }
    }

    fun deleteMatchesBoxSet() {
        viewModelScope.launch {
            dataSource.deleteMatchesBoxSet(matchesBoxSet!!)
        }
        _deletedEvent.value = Event(Unit)
    }

    private fun addMatchesBoxSet(name: String) {
        viewModelScope.launch {
            val newMatchesBoxSet = MatchesBoxSet(name = name, bagId = _bagId)
            dataSource.insertMatchesBoxSet(newMatchesBoxSet)
        }
    }

    private fun updateMatchesBoxSet(name: String) {
        viewModelScope.launch {
            matchesBoxSet?.name = name
            dataSource.updateMatchesBoxSet(matchesBoxSet!!)
        }
    }
}