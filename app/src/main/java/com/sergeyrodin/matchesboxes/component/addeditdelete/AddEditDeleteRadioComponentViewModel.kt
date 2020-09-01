package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.util.Log
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*
import kotlinx.coroutines.launch

const val NO_ID_SET = -1

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource) :
    ViewModel() {
    private val LOG_TAG = javaClass.simpleName
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
        it.toIntOrNull() ?: 0 != 0
    }

    private lateinit var boxTitle: String

    private var matchesBoxId = 0

    private var radioComponent: RadioComponent? = null

    // Bags spinner
    private val bags = MutableLiveData<List<Bag>>()
    val bagNames = bags.map {
        it.map { bag ->
            bag.name
        }
    }
    val noBagsTextVisible = bagNames.map {
        it.isEmpty()
    }

    private val bagIdLiveData = MutableLiveData<Int>()
    val bagSelectedIndex = bagIdLiveData.map { bagId ->
        bags.value?.indexOfFirst { bag ->
            bag.id == bagId
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
    private val setIdLiveData = MutableLiveData<Int>()
    val setSelectedIndex = setIdLiveData.switchMap { setId ->
        sets.map { setsList ->
            setsList.indexOfFirst { set ->
                set.id == setId
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
    private val boxIdLiveData = MutableLiveData<Int>()
    val boxSelectedIndex = boxIdLiveData.switchMap { boxId ->
        boxes.map { boxesList ->
            boxesList.indexOfFirst { box ->
                box.id == boxId
            }
        }
    }

    fun start(boxId: Int, componentId: Int) {
        matchesBoxId = boxId

        if (name.value == null) {
            viewModelScope.launch {
                unpackRadioComponent(componentId)
                updateBagsSpinner()
                Log.i(LOG_TAG, "start called: boxId = $boxId")

                if(boxId != NO_ID_SET){
                    updateSpinnersByBoxId(boxId)
                }else{
                    updateSpinnersByBagId(getFirstBagId())
                }
            }
        }
    }

    private fun getFirstBagId(): Int {
        val firstBagId = if (bags.value?.isNotEmpty() == true) {
            bags.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        }
        return firstBagId
    }

    private suspend fun updateBagsSpinner() {
        bags.value = dataSource.getBags()
    }

    private suspend fun unpackRadioComponent(componentId: Int) {
        radioComponent = dataSource.getRadioComponentById(componentId)
        name.value = radioComponent?.name ?: ""
        quantity.value = (radioComponent?.quantity ?: "").toString()
        isBuy.value = radioComponent?.isBuy ?: false
    }

    fun saveItem() {
        if (name.value?.trim() != "" && matchesBoxId != NO_ID_SET) {
            val nQuantity = quantity.value?.toIntOrNull() ?: 0
            if (nQuantity >= 0) {
                if (radioComponent == null) {
                    addItem(name.value!!, nQuantity)
                } else {
                    updateItem(name.value!!, nQuantity)
                }
            }
        } else {
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
        val nQuantity = quantity.value?.toIntOrNull() ?: 0
        quantity.value = nQuantity.plus(1).toString()
    }

    fun quantityMinus() {
        val nQuantity = quantity.value?.toIntOrNull() ?: 0
        quantity.value = nQuantity.minus(1).toString()
    }

    fun boxSelected(index: Int) {
        matchesBoxId = boxes.value?.get(index)?.id ?: NO_ID_SET
    }

    fun setSelected(newIndex: Int) {
        //Log.i(LOG_TAG, "set selected input: index = ${newIndex}, setSelectedIndex.value = ${setSelectedIndex.value}")
        if (newIndex != setSelectedIndex.value) {
            viewModelScope.launch {
                val setId = sets.value?.get(newIndex)?.id ?: NO_ID_SET
                updateSpinners(inputSetId = setId)
            }
        }
    }

    fun bagSelected(newIndex: Int) {
        Log.i(
            LOG_TAG,
            "bag selected input: newIndex = ${newIndex}, bagSelectedIndex.value = ${bagSelectedIndex.value}"
        )

        if (newIndex != bagSelectedIndex.value) {
            viewModelScope.launch {
                val bagId = bags.value?.get(newIndex)?.id ?: NO_ID_SET
                Log.i(LOG_TAG, "bag selected called: bagId = ${bagId}")
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
            val id = dataSource.insertRadioComponent(component)
            insertHistory(id.toInt(), quantity)
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
            box?.let {
                _updateItemEvent.value = Event(box)
            }
        }
    }

    private suspend fun updateSpinners(
        inputBagId: Int = NO_ID_SET,
        inputSetId: Int = NO_ID_SET,
        inputBoxId: Int = NO_ID_SET
    ) {
        if (inputBoxId != NO_ID_SET) {
            updateSpinnersByBoxId(inputBoxId)
        } else if (inputBagId != NO_ID_SET) {
            updateSpinnersByBagId(inputBagId)
        } else if (inputSetId != NO_ID_SET) {
            updateSpinnersBySetId(inputSetId)
        } else {
            setEmptySpinners()
        }
    }

    private fun setEmptySpinners() {
        bags.value = listOf()
        sets.value = listOf()
        boxes.value = listOf()
    }

    private suspend fun updateSpinnersBySetId(inputSetId: Int) {
        boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(inputSetId)
        matchesBoxId = if (boxes.value?.isNotEmpty() == true) {
            boxes.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        } // select first box
        setIdLiveData.value = inputSetId
        boxIdLiveData.value = matchesBoxId
    }

    private suspend fun updateSpinnersByBagId(inputBagId: Int) {
        sets.value = dataSource.getMatchesBoxSetsByBagId(inputBagId)
        val firstSetId = if (sets.value?.isNotEmpty() == true) {
            sets.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        } // select first set
        boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(firstSetId)
        matchesBoxId = if (boxes.value?.isNotEmpty() == true) {
            boxes.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        } // select first box
        bagIdLiveData.value = inputBagId
        setIdLiveData.value = firstSetId
        boxIdLiveData.value = matchesBoxId
    }

    private suspend fun updateSpinnersByBoxId(inputBoxId: Int) {
        val box = dataSource.getMatchesBoxById(inputBoxId)
        boxTitle = box?.name ?: ""
        val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId ?: NO_ID_SET)
        val bagId = set?.bagId ?: NO_ID_SET
        val setId = set?.id ?: NO_ID_SET
        sets.value = dataSource.getMatchesBoxSetsByBagId(bagId)
        boxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        bagIdLiveData.value = bagId
        setIdLiveData.value = setId
        boxIdLiveData.value = inputBoxId
    }

    private suspend fun insertHistory(componentId: Int, quantity: Int) {
        val history = History(
            componentId = componentId,
            quantity = quantity
        )
        dataSource.insertHistory(history)
    }
}
