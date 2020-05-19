package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteBagViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _eventAdded = MutableLiveData<Event<Unit>>()
    val eventAdded: LiveData<Event<Unit>>
        get() = _eventAdded

    private val _eventEdited = MutableLiveData<Event<Unit>>()
    val eventEdited: LiveData<Event<Unit>>
        get() = _eventEdited

    private val _eventDeleted = MutableLiveData<Event<Unit>>()
    val eventDeleted: LiveData<Event<Unit>>
        get() = _eventDeleted

    private var bag: Bag? = null

    fun start(id: Int?) {
        id?.let {
            viewModelScope.launch{
                bag = dataSource.getBagById(it)
            }
        }
        _name.value = bag?.name?:""
    }

    fun saveBag(newName: String) {
        if(newName.trim() != "") {
            if(bag == null) {
                addNewBag(newName)
            }else{
                updateBag(newName)
            }
        }
    }

    fun deleteBag(){
        viewModelScope.launch {
            dataSource.deleteBag(bag!!)
        }
        _eventDeleted.value = Event(Unit)
    }

    private fun addNewBag(newName: String) {
        viewModelScope.launch {
            val newBag = Bag(name = newName)
            dataSource.insertBag(newBag)
        }
        _eventAdded.value = Event(Unit)
    }

    private fun updateBag(newName: String) {
        viewModelScope.launch {
            bag?.name = newName
            dataSource.updateBag(bag!!)
        }
        _eventEdited.value = Event(Unit)
    }
}