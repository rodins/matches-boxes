package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    val name = MutableLiveData<String>()
    val quantity = MutableLiveData<String>()
    
    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _updateItemEvent = MutableLiveData<Event<Unit>>()
    val updateItemEvent: LiveData<Event<Unit>>
        get() = _updateItemEvent

    private val _deleteItemEvent = MutableLiveData<Event<Unit>>()
    val deleteItemEvent: LiveData<Event<Unit>>
        get() = _deleteItemEvent

    val minusEnabled = Transformations.map(quantity) {
        if(it != ""){
            it.toInt() != 0
        }else {
            false
        }
    }

    private var matchesBoxId = 0

    private var radioComponent: RadioComponent? = null

    fun start(boxId: Int, componentId: Int){
        matchesBoxId = boxId
        viewModelScope.launch {
            radioComponent = dataSource.getRadioComponentById(componentId)
            name.value = radioComponent?.name?:""
            quantity.value = (radioComponent?.quantity?:"").toString()
        }
    }

    fun saveItem() {
        if(name.value?.trim() != "") {
            var nQuantity = 0
            if(quantity.value != ""){
                nQuantity = quantity.value?.toInt()?:0
            }
            if(nQuantity >= 0) {
                if(radioComponent == null) {
                    addItem(name.value!!, nQuantity)
                }else {
                    updateItem(name.value!!, nQuantity)
                }
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            dataSource.deleteRadioComponent(radioComponent!!)
            _deleteItemEvent.value = Event(Unit)
        }
    }

    fun quantityPlus() {
        val nQuantity = quantity.value?.toInt()
        quantity.value = nQuantity?.plus(1).toString()
    }

    fun quantityMinus() {
        val nQuantity = quantity.value?.toInt()
        quantity.value = nQuantity?.minus(1).toString()
    }

    private fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val component = RadioComponent(
                name = name,
                quantity = quantity,
                matchesBoxId = matchesBoxId)
            dataSource.insertRadioComponent(component)
            _addItemEvent.value = Event(Unit)
        }
    }

    private fun updateItem(name: String, quantity: Int) {
        viewModelScope.launch {
            radioComponent?.name = name
            radioComponent?.quantity = quantity
            dataSource.updateRadioComponent(radioComponent!!)
            _updateItemEvent.value = Event(Unit)
        }
    }

}