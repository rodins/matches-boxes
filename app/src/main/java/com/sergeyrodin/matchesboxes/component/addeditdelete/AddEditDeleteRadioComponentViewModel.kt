package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _quantity = MutableLiveData<String>()
    val quantity: LiveData<String>
        get() = _quantity
    
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
        it.toInt() != 0
    }

    private var matchesBoxId = 0

    private var radioComponent: RadioComponent? = null

    fun start(boxId: Int, componentId: Int){
        matchesBoxId = boxId
        viewModelScope.launch {
            radioComponent = dataSource.getRadioComponentById(componentId)
            _name.value = radioComponent?.name?:""
            _quantity.value = (radioComponent?.quantity?:0).toString()
        }
    }

    fun saveItem(name: String, quantity: String) {
        if(name.trim() != "") {
            val nQuantity = quantity.toInt()
            if(nQuantity >= 0) {
                if(radioComponent == null) {
                    addItem(name, nQuantity)
                }else {
                    updateItem(name, nQuantity)
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
        _quantity.value = nQuantity?.plus(1).toString()
    }

    fun quantityMinus() {
        val nQuantity = quantity.value?.toInt()
        _quantity.value = nQuantity?.minus(1).toString()
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