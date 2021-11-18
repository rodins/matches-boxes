package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesBoxManipulatorViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource) :
    ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private var matchesBox: MatchesBox? = null
    private var matchesBoxSetIdForNewMatchesBox: Int = 0

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
        if(_name.value == null) {
            matchesBoxSetIdForNewMatchesBox = setId
            viewModelScope.launch {
                getMatchesBoxById(boxId)
                displayMatchesBoxName()
            }
        }
    }

    private fun displayMatchesBoxName() {
        _name.value = matchesBox?.name ?: ""
    }

    private suspend fun getMatchesBoxById(boxId: Int) {
        matchesBox = dataSource.getMatchesBoxById(boxId)
    }

    fun saveMatchesBox() {
        if (_name.value?.trim() != "") {
            addOrUpdateMatchesBox()
        }
    }

    fun setNewName(newName: String) {
        _name.value = newName
    }

    private fun addOrUpdateMatchesBox() {
        if (matchesBox == null) {
            addMatchesBox()
        } else {
            updateMatchesBox()
        }
    }

    private fun addMatchesBox() {
        viewModelScope.launch {
            insertMatchesBox()
            callAddEvent()
        }
    }

    private suspend fun insertMatchesBox() {
        val box = MatchesBox(name = _name.value!!, matchesBoxSetId = matchesBoxSetIdForNewMatchesBox)
        dataSource.insertMatchesBox(box)
    }

    private suspend fun callAddEvent() {
        val name = getMatchesBoxSetNameById()
        callAddEventWithMatchesBoxSetName(name)
    }

    private suspend fun getMatchesBoxSetNameById(): String {
        val set = dataSource.getMatchesBoxSetById(matchesBoxSetIdForNewMatchesBox)
        return set?.name ?: ""
    }

    private fun callAddEventWithMatchesBoxSetName(name: String) {
        _addEvent.value = Event(name)
    }

    private fun updateMatchesBox() {
        viewModelScope.launch {
            updateMatchesBoxInDb()
            callUpdateEvent()
        }
    }

    private suspend fun updateMatchesBoxInDb() {
        matchesBox?.name = _name.value!!
        dataSource.updateMatchesBox(matchesBox!!)
    }

    private fun callUpdateEvent() {
        _updateEvent.value = Event(_name.value!!)
    }

    fun deleteMatchesBox() {
        viewModelScope.launch {
            val id = getMatchesBoxSetIdOfMatchesBoxBeforeDeleted()
            deleteMatchesBoxIdDb()
            callDeleteEvent(id)
        }
    }

    private fun getMatchesBoxSetIdOfMatchesBoxBeforeDeleted(): Int? {
        return matchesBox?.matchesBoxSetId
    }

    private suspend fun deleteMatchesBoxIdDb() {
        dataSource.deleteMatchesBox(matchesBox!!)
    }

    private suspend fun callDeleteEvent(id: Int?) {
        callDeleteEventWithMatchesBoxSet(getMatchesBoxSetById(id))
    }

    private suspend fun getMatchesBoxSetById(id: Int?): MatchesBoxSet? {
        return dataSource.getMatchesBoxSetById(id!!)
    }

    private fun callDeleteEventWithMatchesBoxSet(set: MatchesBoxSet?) {
        _deleteEvent.value = Event(set!!)
    }
}