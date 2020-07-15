package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    val name = MutableLiveData<String>()
    val quantity = MutableLiveData<String>()
    
    private val _addItemEvent = MutableLiveData<Event<String>>()
    val addItemEvent: LiveData<Event<String>>
        get() = _addItemEvent

    private val _updateItemEvent = MutableLiveData<Event<String>>()
    val updateItemEvent: LiveData<Event<String>>
        get() = _updateItemEvent

    private val _deleteItemEvent = MutableLiveData<Event<String>>()
    val deleteItemEvent: LiveData<Event<String>>
        get() = _deleteItemEvent

    val minusEnabled = Transformations.map(quantity) {
        it.toIntOrNull()?:0 != 0
    }

    private lateinit var boxTitle: String

    private var matchesBoxId = 0

    private var radioComponent: RadioComponent? = null

    val isBuy = MutableLiveData<Boolean>()

    // Bags spinner
    private val _bagNames = MutableLiveData<List<String>>()
    val bagNames: LiveData<List<String>>
        get() = _bagNames

    private val _bagSelectedIndex = MutableLiveData<Int>()
    val bagSelectedIndex: LiveData<Int>
        get() = _bagSelectedIndex

    // Sets spinner
    private lateinit var sets: List<MatchesBoxSet>
    private val _setNames = MutableLiveData<List<String>>()
    val setNames: LiveData<List<String>>
        get() = _setNames

    private val _setSelectedIndex = MutableLiveData<Int>()
    val setSelectedIndex: LiveData<Int>
        get() = _setSelectedIndex

    // Boxes spinner
    private val _boxNames = MutableLiveData<List<String>>()
    val boxNames: LiveData<List<String>>
        get() = _boxNames

    private val _boxSelectedIndex = MutableLiveData<Int>()
    val boxSelectedIndex: LiveData<Int>
        get() = _boxSelectedIndex

    fun start(boxId: Int, componentId: Int){
        matchesBoxId = boxId
        viewModelScope.launch {
            radioComponent = dataSource.getRadioComponentById(componentId)
            name.value = radioComponent?.name?:""
            quantity.value = (radioComponent?.quantity?:"").toString()
            isBuy.value = radioComponent?.isBuy?:false

            val box = dataSource.getMatchesBoxById(boxId)
            boxTitle = box?.name?:""

            box?.let{
                val boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(box.matchesBoxSetId)
                _boxNames.value = boxes.map {
                    it.name
                }
                _boxSelectedIndex.value = boxes.indexOfFirst {
                    it.id == boxId
                }

                val set = dataSource.getMatchesBoxSetById(box.matchesBoxSetId)
                set?.let {
                    sets = dataSource.getMatchesBoxSetsByBagId(set.bagId)
                    _setNames.value = sets.map {
                        it.name
                    }

                    _setSelectedIndex.value = sets.indexOfFirst {
                        it.id == set.id
                    }

                    val bags = dataSource.getBags()
                    _bagNames.value = bags.map {
                        it.name
                    }

                    _bagSelectedIndex.value = bags.indexOfFirst {
                        it.id == set.bagId
                    }
                }
            }
        }
    }

    fun saveItem() {
        if(name.value?.trim() != "") {
            val nQuantity = quantity.value?.toIntOrNull()?:0
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
            _deleteItemEvent.value = Event(boxTitle)
        }
    }

    fun quantityPlus() {
        val nQuantity = quantity.value?.toIntOrNull()?:0
        quantity.value = nQuantity.plus(1).toString()
    }

    fun quantityMinus() {
        val nQuantity = quantity.value?.toIntOrNull()?:0
        quantity.value = nQuantity.minus(1).toString()
    }

    fun setSelected(index: Int) {
        if(index != setSelectedIndex.value) {
            viewModelScope.launch {
                val set = sets[index]
                val boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
                _boxNames.value = boxes.map {
                    it.name
                }
                _boxSelectedIndex.value = 0
            }
        }

    }

    private fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val component = RadioComponent(
                name = name,
                quantity = quantity,
                matchesBoxId = matchesBoxId,
                isBuy = isBuy.value!!
            )
            dataSource.insertRadioComponent(component)
            _addItemEvent.value = Event(boxTitle)
        }
    }

    private fun updateItem(name: String, quantity: Int) {
        viewModelScope.launch {
            radioComponent?.name = name
            radioComponent?.quantity = quantity
            radioComponent?.isBuy = isBuy.value!!
            dataSource.updateRadioComponent(radioComponent!!)
            _updateItemEvent.value = Event(boxTitle)
        }
    }

}