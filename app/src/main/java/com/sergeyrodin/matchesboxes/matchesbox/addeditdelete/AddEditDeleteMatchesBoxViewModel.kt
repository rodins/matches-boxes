package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteMatchesBoxViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    val name = MutableLiveData<String>()

    private var matchesBox: MatchesBox? = null
    private var _setId: Int = 0

    private val _addEvent = MutableLiveData<Event<String>>()
    val addEvent: LiveData<Event<String>>
        get() = _addEvent

    private val _updateEvent = MutableLiveData<Event<String>>()
    val updateEvent: LiveData<Event<String>>
        get() = _updateEvent

    private val _deleteEvent = MutableLiveData<Event<MatchesBoxSet>>()
    val deleteEvent: LiveData<Event<MatchesBoxSet>>
        get() = _deleteEvent

    fun start(setId: Int, boxId: Int) {
        _setId = setId
       viewModelScope.launch {
           matchesBox = dataSource.getMatchesBoxById(boxId)
           name.value = matchesBox?.name?:""
       }
    }

    fun saveMatchesBox() {
        if(name.value?.trim() != "") {
            if(matchesBox == null) {
                addMatchesBox()
            }else {
                updateMatchesBox()
            }
        }
    }

    fun deleteMatchesBox() {
        viewModelScope.launch {
            val id = matchesBox?.matchesBoxSetId
            dataSource.deleteMatchesBox(matchesBox!!)
            val set = dataSource.getMatchesBoxSetById(id!!)
            _deleteEvent.value = Event(set!!)
        }
    }

    private fun addMatchesBox() {
        viewModelScope.launch {
            val box = MatchesBox(name = name.value!!, matchesBoxSetId = _setId)
            dataSource.insertMatchesBox(box)
            val set = dataSource.getMatchesBoxSetById(_setId)
            _addEvent.value = Event(set?.name?:"")
        }
    }

    private fun updateMatchesBox() {
        viewModelScope.launch {
            matchesBox?.name = name.value!!
            dataSource.updateMatchesBox(matchesBox!!)
            _updateEvent.value = Event(name.value!!)
        }
    }
}