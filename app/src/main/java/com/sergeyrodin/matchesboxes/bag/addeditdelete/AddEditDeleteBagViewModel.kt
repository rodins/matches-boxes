package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteBagViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _eventAdded = MutableLiveData(false)
    val eventAdded: LiveData<Boolean>
        get() = _eventAdded

    private val _eventEdited = MutableLiveData(false)
    val eventEdited: LiveData<Boolean>
        get() = _eventEdited

    private val _eventDeleted = MutableLiveData(false)
    val eventDeleted: LiveData<Boolean>
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
        _eventDeleted.value = true
    }

    private fun addNewBag(newName: String) {
        viewModelScope.launch {
            val newBag = Bag(name = newName)
            dataSource.insertBag(newBag)
        }
        _eventAdded.value = true
    }

    private fun updateBag(newName: String) {
        viewModelScope.launch {
            bag?.name = newName
            dataSource.updateBag(bag!!)
        }
        _eventEdited.value = true
    }

    fun doneEventAdded() {
        _eventAdded.value = false
    }

    fun doneEventEdited() {
        _eventEdited.value = false
    }

    fun doneEventDeleted() {
        _eventDeleted.value = false
    }
}