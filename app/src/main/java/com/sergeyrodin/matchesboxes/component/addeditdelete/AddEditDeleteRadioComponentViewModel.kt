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
    val bagNames = bags.switchMap{
        liveData{
            emit(it.map{ bag ->
                bag.name
            })
        }
    }
    private val _bagSelectedIndex = MutableLiveData<Int>()
    val bagSelectedIndex: LiveData<Int>
        get() = _bagSelectedIndex
    val noBagsTextVisible = bagNames.map {
        it.isEmpty()
    }

    // Sets spinner
    private lateinit var sets: List<MatchesBoxSet>
    private val _setNames = MutableLiveData<List<String>>()
    val setNames: LiveData<List<String>>
        get() = _setNames
    val noSetsTextVisible = setNames.map {
        it.isEmpty()
    }
    private val _setSelectedIndex = MutableLiveData<Int>()
    val setSelectedIndex: LiveData<Int>
        get() = _setSelectedIndex

    // Boxes spinner
    private lateinit var boxes: List<MatchesBox>
    private val _boxNames = MutableLiveData<List<String>>()
    val boxNames: LiveData<List<String>>
        get() = _boxNames
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
                    sets = dataSource.getMatchesBoxSetsByBagId(bags.value!![0].id)
                    _setNames.value = sets.map {
                        it.name
                    }
                    if(sets.isNotEmpty()) {
                        _setSelectedIndex.value = 0
                        boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(sets[0].id)
                        _boxNames.value = boxes.map {
                            it.name
                        }
                        if(boxes.isNotEmpty()){
                            _boxSelectedIndex.value = 0
                        }
                    }else {
                        boxes = listOf()
                        _boxNames.value = boxes.map {
                            it.name
                        }
                    }
                }else {
                    sets = listOf()
                    _setNames.value = sets.map {
                        it.name
                    }
                    boxes = listOf()
                    _boxNames.value = boxes.map {
                        it.name
                    }
                }
            }else {
                val box = dataSource.getMatchesBoxById(boxId)
                boxTitle = box?.name?:""

                box?.let{
                    boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(box.matchesBoxSetId)
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
        matchesBoxId.value = boxes[index].id
    }

    fun setSelected(index: Int) {
        if(index != setSelectedIndex.value) {
            viewModelScope.launch {
                val set = sets[index]
                boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
                if(boxes.isNotEmpty()) {
                    _boxNames.value = boxes.map {
                        it.name
                    }
                    _boxSelectedIndex.value = 0
                    matchesBoxId.value = boxes[0].id
                }else {
                    _boxNames.value = listOf()
                }
            }
        }
    }

    fun bagSelected(index: Int) {
        if(index != bagSelectedIndex.value) {
            viewModelScope.launch {
                val bag = dataSource.getBags()[index]
                sets = dataSource.getMatchesBoxSetsByBagId(bag.id)
                if(sets.isNotEmpty()) {
                    _setNames.value = sets.map{
                        it.name
                    }
                    _setSelectedIndex.value = 0
                    val set = sets[0]
                    boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
                    _boxNames.value = boxes.map {
                        it.name
                    }
                    _boxSelectedIndex.value = 0
                    matchesBoxId.value = boxes[0].id
                }else {
                    _setNames.value = listOf()
                    _boxNames.value = listOf()
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
            val box = boxes.find{
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
            val box = boxes.find{
                it.id == matchesBoxId.value!!
            }
            box?.let {
                _updateItemEvent.value = Event(it)
            }
        }
    }

}