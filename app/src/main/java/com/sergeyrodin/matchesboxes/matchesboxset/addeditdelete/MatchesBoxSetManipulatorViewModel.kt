package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesBoxSetManipulatorViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource): ViewModel() {

    private var matchesBoxSet: MatchesBoxSet? = null
    private var bagIdForNewMatchesBoxSet: Int = 0

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
        if(name.value == null) {
            bagIdForNewMatchesBoxSet = bagId
            viewModelScope.launch {
                getMatchesBoxSetById(setId)
                getMatchesBoxSetNameToDisplay()
            }
        }
    }

    private suspend fun getMatchesBoxSetById(setId: Int) {
        matchesBoxSet = dataSource.getMatchesBoxSetById(setId)
    }

    private fun getMatchesBoxSetNameToDisplay() {
        name.value = matchesBoxSet?.name ?: ""
    }

    fun saveMatchesBoxSet() {
        if(name.value?.trim() != "") {
            addOrUpdateMatchesBoxSet()
        }
    }

    private fun addOrUpdateMatchesBoxSet() {
        if (matchesBoxSet == null) {
            addMatchesBoxSet()
        } else {
            updateMatchesBoxSet()
        }
    }

    private fun addMatchesBoxSet() {
        viewModelScope.launch {
            insertMatchesBoxSetToDb()
            callAddedEvent()
        }
    }

    private suspend fun insertMatchesBoxSetToDb() {
        val newMatchesBoxSet = MatchesBoxSet(name = name.value!!, bagId = bagIdForNewMatchesBoxSet)
        dataSource.insertMatchesBoxSet(newMatchesBoxSet)
    }

    private suspend fun callAddedEvent() {
        val bagName = getBagNameById()
        callAddedEventWithBagName(bagName)
    }

    private suspend fun getBagNameById(): String {
        val bag = dataSource.getBagById(bagIdForNewMatchesBoxSet)
        return bag?.name ?: ""
    }

    private fun callAddedEventWithBagName(bagName: String) {
        _addedEvent.value = Event(bagName)
    }

    private fun updateMatchesBoxSet() {
        viewModelScope.launch {
            updateMatchesBoxSetInDb()
            callUpdatedEvent()
        }
    }

    private suspend fun updateMatchesBoxSetInDb() {
        matchesBoxSet?.name = name.value!!
        dataSource.updateMatchesBoxSet(matchesBoxSet!!)
    }

    private fun callUpdatedEvent() {
        _updatedEvent.value = Event(matchesBoxSet!!)
    }

    fun deleteMatchesBoxSet() {
        viewModelScope.launch {
            val bagId = getBagIdOfMatchesBoxSetToBeDeleted()
            deleteMatchesBoxSetFromDb()
            callDeleteEventWithBagId(bagId)
        }
    }

    private fun getBagIdOfMatchesBoxSetToBeDeleted(): Int? {
        return matchesBoxSet?.bagId
    }

    private suspend fun deleteMatchesBoxSetFromDb() {
        dataSource.deleteMatchesBoxSet(matchesBoxSet!!)
    }

    private suspend fun callDeleteEventWithBagId(bagId: Int?) {
        val bag = getBagById(bagId)
        callDeleteEvent(bag)
    }

    private suspend fun getBagById(bagId: Int?): Bag? {
        return dataSource.getBagById(bagId!!)
    }

    private fun callDeleteEvent(bag: Bag?) {
        bag?.let {
            _deletedEvent.value = Event(bag)
        }
    }
}