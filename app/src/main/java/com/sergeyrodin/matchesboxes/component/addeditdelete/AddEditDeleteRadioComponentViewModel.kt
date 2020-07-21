package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*
import kotlinx.coroutines.launch

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    val name = MutableLiveData<String>()
    val quantity = MutableLiveData<String>()
    val isBuy = MutableLiveData<Boolean>()
    
    private val _addItemEvent = MutableLiveData<Event<MatchesBox>>()
    val addItemEvent: LiveData<Event<MatchesBox>>
        get() = _addItemEvent

    private val _updateItemEvent = MutableLiveData<Event<MatchesBox>>()
    val updateItemEvent: LiveData<Event<MatchesBox>>
        get() = _updateItemEvent

    private val _deleteItemEvent = MutableLiveData<Event<String>>()
    val deleteItemEvent: LiveData<Event<String>>
        get() = _deleteItemEvent

    val minusEnabled = Transformations.map(quantity) {
        it.toIntOrNull()?:0 != 0
    }

    private lateinit var boxTitle: String

    private val matchesBoxId = MutableLiveData<Int>()

    private var radioComponent: RadioComponent? = null

    // Bags spinner
    private val bags = MutableLiveData<List<Bag>>()
    val bagNames = bags.map{
        it.map{ bag ->
            bag.name
        }
    }
    private val _bagSelectedIndex = MutableLiveData<Int>()
    val bagSelectedIndex: LiveData<Int>
        get() = _bagSelectedIndex
    val noBagsTextVisible = bagNames.map {
        it.isEmpty()
    }

    // Sets spinner
    private val sets = MutableLiveData<List<MatchesBoxSet>>()
    val setNames = sets.map {
        it.map { set ->
            set.name
        }
    }
    val noSetsTextVisible = setNames.map {
        it.isEmpty()
    }
    private val _setSelectedIndex = MutableLiveData<Int>()
    val setSelectedIndex: LiveData<Int>
        get() = _setSelectedIndex

    // Boxes spinner
    private val boxes = MutableLiveData<List<MatchesBox>>()
    val boxNames = boxes.map {
        it.map { box ->
            box.name
        }
    }
    val noBoxesTextVisible = boxNames.map {
        it.isEmpty()
    }
    private val _boxSelectedIndex = MutableLiveData<Int>()
    val boxSelectedIndex: LiveData<Int>
        get() = _boxSelectedIndex

    fun start(boxId: Int, componentId: Int){
        matchesBoxId.value = boxId
        viewModelScope.launch {
            radioComponent = dataSource.getRadioComponentById(componentId)
            name.value = radioComponent?.name?:""
            quantity.value = (radioComponent?.quantity?:"").toString()
            isBuy.value = radioComponent?.isBuy?:false

            bags.value = dataSource.getBags()

            if(boxId == ADD_NEW_ITEM_ID) {
                if(bags.value!!.isNotEmpty()) {
                    _bagSelectedIndex.value = 0
                    sets.value = dataSource.getMatchesBoxSetsByBagId(bags.value!![0].id)
                    if(sets.value!!.isNotEmpty()) {
                        _setSelectedIndex.value = 0
                        boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(sets.value!![0].id)
                        if(boxes.value!!.isNotEmpty()){
                            _boxSelectedIndex.value = 0
                        }
                    }else {
                        boxes.value = listOf()
                    }
                }else {
                    sets.value = listOf()
                    boxes.value = listOf()
                }
            }else {
                val box = dataSource.getMatchesBoxById(boxId)
                boxTitle = box?.name?:""

                box?.let{
                    boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(box.matchesBoxSetId)
                    _boxSelectedIndex.value = boxes.value!!.indexOfFirst {
                        it.id == boxId
                    }

                    val set = dataSource.getMatchesBoxSetById(box.matchesBoxSetId)
                    set?.let {
                        sets.value = dataSource.getMatchesBoxSetsByBagId(set.bagId)

                        _setSelectedIndex.value = sets.value!!.indexOfFirst {
                            it.id == set.id
                        }

                        _bagSelectedIndex.value = bags.value?.indexOfFirst {
                            it.id == set.bagId
                        }
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

    fun boxSelected(index: Int) {
        matchesBoxId.value = boxes.value!![index].id
    }

    fun setSelected(index: Int) {
        if(index != setSelectedIndex.value) {
            viewModelScope.launch {
                val set = sets.value!![index]
                boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
                if(boxes.value!!.isNotEmpty()) {
                    _boxSelectedIndex.value = 0
                    matchesBoxId.value = boxes.value!![0].id
                }
            }
        }
    }

    fun bagSelected(index: Int) {
        if(index != bagSelectedIndex.value) {
            viewModelScope.launch {
                val bag = bags.value!![index]
                sets.value = dataSource.getMatchesBoxSetsByBagId(bag.id)
                if(sets.value!!.isNotEmpty()) {
                    _setSelectedIndex.value = 0
                    val set = sets.value!![0]
                    boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
                    _boxSelectedIndex.value = 0
                    matchesBoxId.value = boxes.value!![0].id
                }else{
                    boxes.value = listOf()
                }
            }
        }
    }

    private fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val component = RadioComponent(
                name = name,
                quantity = quantity,
                matchesBoxId = matchesBoxId.value!!,
                isBuy = isBuy.value!!
            )
            dataSource.insertRadioComponent(component)
            val box = boxes.value!!.find{
                it.id == matchesBoxId.value!!
            }
            box?.let {
                _addItemEvent.value = Event(it)
            }
        }
    }

    private fun updateItem(name: String, quantity: Int) {
        viewModelScope.launch {
            radioComponent?.name = name
            radioComponent?.quantity = quantity
            radioComponent?.matchesBoxId = matchesBoxId.value!!
            radioComponent?.isBuy = isBuy.value!!
            dataSource.updateRadioComponent(radioComponent!!)
            val box = boxes.value!!.find{
                it.id == matchesBoxId.value!!
            }
            box?.let {
                _updateItemEvent.value = Event(it)
            }
        }
    }

}