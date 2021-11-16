package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BagManipulatorViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource) : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _eventAdded = MutableLiveData<Event<Unit>>()
    val eventAdded: LiveData<Event<Unit>>
        get() = _eventAdded

    private val _eventEdited = MutableLiveData<Event<String>>()
    val eventEdited: LiveData<Event<String>>
        get() = _eventEdited

    private val _eventDeleted = MutableLiveData<Event<Unit>>()
    val eventDeleted: LiveData<Event<Unit>>
        get() = _eventDeleted

    private var bag: Bag? = null

    fun start(id: Int) {
        if(name.value == null) {
            viewModelScope.launch{
                getBagById(id)
                getBagNameToDisplay()
            }
        }
    }

    private suspend fun getBagById(id: Int) {
        bag = dataSource.getBagById(id)
    }

    private fun getBagNameToDisplay() {
        _name.value = bag?.name ?: ""
    }

    fun saveBag() {
        if(_name.value?.trim() != "") {
            addOrUpdateBag()
        }
    }

    private fun addOrUpdateBag() {
        if (bag == null) {
            addNewBag()
        } else {
            updateBag()
        }
    }

    private fun addNewBag() {
        viewModelScope.launch {
            insertBagToDb()
            callAddEvent()
        }
    }

    private suspend fun insertBagToDb() {
        val newBag = Bag(name = _name.value!!)
        dataSource.insertBag(newBag)
    }

    private fun callAddEvent() {
        _eventAdded.value = Event(Unit)
    }

    private fun updateBag() {
        viewModelScope.launch {
            updateBagInDb()
            callUpdateEvent()
        }
    }

    private suspend fun updateBagInDb() {
        bag?.name = _name.value!!
        dataSource.updateBag(bag!!)
    }

    private fun callUpdateEvent() {
        _eventEdited.value = Event(_name.value!!)
    }

    fun deleteBag(){
        viewModelScope.launch {
            deleteBagFromDb()
            callDeleteEvent()
        }
    }

    private suspend fun deleteBagFromDb() {
        dataSource.deleteBag(bag!!)
    }

    private fun callDeleteEvent() {
        _eventDeleted.value = Event(Unit)
    }

    fun setNewName(newName: String) {
        _name.value = newName
    }
}