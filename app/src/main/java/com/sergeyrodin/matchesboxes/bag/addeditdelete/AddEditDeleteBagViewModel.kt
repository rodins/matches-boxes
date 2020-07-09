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

    val name = MutableLiveData<String>()

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
        viewModelScope.launch{
            bag = dataSource.getBagById(id)
            name.value = bag?.name?:""
        }
    }

    fun saveBag() {
        if(name.value?.trim() != "") {
            if(bag == null) {
                addNewBag()
            }else{
                updateBag()
            }
        }
    }

    fun deleteBag(){
        viewModelScope.launch {
            dataSource.deleteBag(bag!!)
            _eventDeleted.value = Event(Unit)
        }
    }

    private fun addNewBag() {
        viewModelScope.launch {
            val newBag = Bag(name = name.value!!)
            dataSource.insertBag(newBag)
            _eventAdded.value = Event(Unit)
        }
    }

    private fun updateBag() {
        viewModelScope.launch {
            bag?.name = name.value!!
            dataSource.updateBag(bag!!)
            _eventEdited.value = Event(name.value!!)
        }
    }
}