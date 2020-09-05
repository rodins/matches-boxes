package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class MatchesBoxSetManipulatorViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    private var matchesBoxSet: MatchesBoxSet? = null
    private var _bagId: Int = 0

    private val _addedEvent = MutableLiveData<Event<String>>()
    val addedEvent: LiveData<Event<String>>
        get() = _addedEvent

    private val _updatedEvent = MutableLiveData<Event<MatchesBoxSet>>()
    val updatedEvent: LiveData<Event<MatchesBoxSet>>
        get() = _updatedEvent

    private val _deletedEvent = MutableLiveData<Event<Bag>>()
    val deletedEvent: LiveData<Event<Bag>>
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
            val bagId = matchesBoxSet?.bagId
            dataSource.deleteMatchesBoxSet(matchesBoxSet!!)
            val bag = dataSource.getBagById(bagId!!)
            bag?.let {
                _deletedEvent.value = Event(bag)
            }
        }
    }

    private fun addMatchesBoxSet() {
        viewModelScope.launch {
            val newMatchesBoxSet = MatchesBoxSet(name = name.value!!, bagId = _bagId)
            dataSource.insertMatchesBoxSet(newMatchesBoxSet)
            val bag = dataSource.getBagById(_bagId)
            _addedEvent.value = Event(bag?.name?:"")
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