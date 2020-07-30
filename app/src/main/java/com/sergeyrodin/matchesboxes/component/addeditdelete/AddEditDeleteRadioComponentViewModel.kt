package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*
import kotlinx.coroutines.launch

const val NO_ID_SET = -1

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

    private val _errorEvent = MutableLiveData<Event<Unit>>()
    val errorEvent: LiveData<Event<Unit>>
        get() = _errorEvent

    val minusEnabled = Transformations.map(quantity) {
        it.toIntOrNull()?:0 != 0
    }

    private lateinit var boxTitle: String

    private var matchesBoxId = 0

    private var radioComponent: RadioComponent? = null

    private val selectedIndexIds = MutableLiveData<ThreeIds>()

    // Bags spinner
    private val bags = MutableLiveData<List<Bag>>()
    val bagNames = bags.map{
        it.map{ bag ->
            bag.name
        }
    }
    val noBagsTextVisible = bagNames.map {
        it.isEmpty()
    }
    val bagSelectedIndex = selectedIndexIds.switchMap {
        bags.map { bagsList ->
            bagsList.indexOfFirst { bag ->
                bag.id == it.bagId
            }
        }
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
    val setSelectedIndex = selectedIndexIds.switchMap{
        sets.map { setsList ->
            setsList.indexOfFirst { set ->
                set.id == it.setId
            }
        }
    }

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
    val boxSelectedIndex = selectedIndexIds.switchMap {
        boxes.map { boxesList ->
            boxesList.indexOfFirst { box ->
                box.id == it.boxId
            }
        }
    }

    fun start(boxId: Int, componentId: Int){
        matchesBoxId = boxId

        viewModelScope.launch {
            radioComponent = dataSource.getRadioComponentById(componentId)
            name.value = radioComponent?.name?:""
            quantity.value = (radioComponent?.quantity?:"").toString()
            isBuy.value = radioComponent?.isBuy?:false

            bags.value = dataSource.getBags()
            val firstBagId = if(bags.value?.isNotEmpty() == true) bags.value?.get(0)?.id?: NO_ID_SET else NO_ID_SET
            updateSpinners(inputBagId = firstBagId, inputBoxId = boxId)
        }
    }

    fun saveItem() {
        if(name.value?.trim() != "" && matchesBoxId != NO_ID_SET) {
            val nQuantity = quantity.value?.toIntOrNull()?:0
            if(nQuantity >= 0) {
                if(radioComponent == null) {
                    addItem(name.value!!, nQuantity)
                }else {
                    updateItem(name.value!!, nQuantity)
                }
            }
        }else {
            _errorEvent.value = Event(Unit)
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
        matchesBoxId = boxes.value?.get(index)?.id?: NO_ID_SET
    }

    fun setSelected(index: Int) {
        if(index != setSelectedIndex.value) {
            viewModelScope.launch {
                val setId = sets.value?.get(index)?.id?: NO_ID_SET
                updateSpinners(inputSetId = setId)
            }
        }
    }

    fun bagSelected(index: Int) {
        if(index != bagSelectedIndex.value) {
            viewModelScope.launch {
                val bagId = bags.value?.get(index)?.id?: NO_ID_SET
                updateSpinners(inputBagId = bagId)
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
            val box = dataSource.getMatchesBoxById(matchesBoxId)
            box?.let {
                _addItemEvent.value = Event(it)
            }
        }
    }

    private fun updateItem(name: String, quantity: Int) {
        viewModelScope.launch {
            radioComponent?.name = name
            radioComponent?.quantity = quantity
            radioComponent?.matchesBoxId = matchesBoxId
            radioComponent?.isBuy = isBuy.value!!
            dataSource.updateRadioComponent(radioComponent!!)
            insertHistory(radioComponent!!.id, radioComponent!!.quantity)
            val box = dataSource.getMatchesBoxById(matchesBoxId)
            box?.let{
                _updateItemEvent.value = Event(box)
            }
        }
    }

    private suspend fun updateSpinners(inputBagId: Int = NO_ID_SET, inputSetId: Int = NO_ID_SET, inputBoxId: Int = NO_ID_SET) {
        if(inputBoxId != NO_ID_SET) { // if we have component or box get it's path
            val box = dataSource.getMatchesBoxById(inputBoxId)
            boxTitle = box?.name?:""
            val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId?: NO_ID_SET)
            val bagId = set?.bagId?: NO_ID_SET
            val setId = set?.id?:NO_ID_SET
            sets.value = dataSource.getMatchesBoxSetsByBagId(bagId)
            boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
            selectedIndexIds.value = ThreeIds(bagId, setId, inputBoxId)
        }else if(inputBagId != NO_ID_SET) { // if we have no component and no box defined or bag is changed
            sets.value = dataSource.getMatchesBoxSetsByBagId(inputBagId)
            val firstSetId = if(sets.value?.isNotEmpty() == true) sets.value?.get(0)?.id?: NO_ID_SET else NO_ID_SET // select first set
            boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(firstSetId)
            matchesBoxId = if(boxes.value?.isNotEmpty() == true) boxes.value?.get(0)?.id?: NO_ID_SET else NO_ID_SET // select first box
            selectedIndexIds.value = ThreeIds(inputBagId, firstSetId, matchesBoxId)
        }else if(inputSetId != NO_ID_SET) {  // if set is changed
            boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(inputSetId)
            matchesBoxId = if(boxes.value?.isNotEmpty() == true) boxes.value?.get(0)?.id?: NO_ID_SET else NO_ID_SET // select first box
            selectedIndexIds.value = ThreeIds(inputBagId, inputSetId, matchesBoxId)
        }else{ // Set empty lists
            bags.value = listOf()
            sets.value = listOf()
            boxes.value = listOf()
        }
    }

    private suspend fun insertHistory(componentId: Int, quantity: Int) {
        val history = History(
            componentId = componentId,
            quantity = quantity
        )
        dataSource.insertHistory(history)
    }
}

data class ThreeIds(
    val bagId: Int = NO_ID_SET,
    val setId: Int = NO_ID_SET,
    val boxId: Int = NO_ID_SET
)