package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteMatchesBoxViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private var matchesBox: MatchesBox? = null
    private var _setId: Int = 0

    private val _addEvent = MutableLiveData<Event<Unit>>()
    val addEvent: LiveData<Event<Unit>>
        get() = _addEvent

    private val _updateEvent = MutableLiveData<Event<Unit>>()
    val updateEvent: LiveData<Event<Unit>>
        get() = _updateEvent

    private val _deleteEvent = MutableLiveData<Event<Unit>>()
    val deleteEvent: LiveData<Event<Unit>>
        get() = _deleteEvent

    fun start(setId: Int, boxId: Int) {
        _setId = setId
       viewModelScope.launch {
           matchesBox = dataSource.getMatchesBoxById(boxId)
           _name.value = matchesBox?.name?:""
       }
    }

    fun saveMatchesBox(boxName: String) {
        if(boxName.trim() != "") {
            if(matchesBox == null) {
                addMatchesBox(boxName)
                _addEvent.value = Event(Unit)
            }else {
                updateMatchesBox(boxName)
                _updateEvent.value = Event(Unit)
            }
        }
    }

    fun deleteMatchesBox() {
        viewModelScope.launch {
            dataSource.deleteMatchesBox(matchesBox!!)
            _deleteEvent.value = Event(Unit)
        }
    }

    private fun addMatchesBox(boxName: String) {
        viewModelScope.launch {
            matchesBox = MatchesBox(name = boxName, matchesBoxSetId = _setId)
            dataSource.insertMatchesBox(matchesBox!!)
        }
    }

    private fun updateMatchesBox(boxName: String) {
        viewModelScope.launch {
            matchesBox?.name = boxName
            dataSource.updateMatchesBox(matchesBox!!)
        }
    }
}