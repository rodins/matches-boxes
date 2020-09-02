package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.util.Log
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*
import kotlinx.coroutines.launch

const val NO_ID_SET = -1
private const val NO_INDEX_SET = -1
private const val FIRST_INDEX = 0

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource) :
    ViewModel() {
    private val tag = javaClass.simpleName
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
    private var currentBagSelectedIndex = NO_INDEX_SET
    private lateinit var bags: List<Bag>
    private val observableBags = MutableLiveData<List<Bag>>()
    val bagNames = observableBags.map {
        it.map { bag ->
            bag.name
        }
    }
    val noBagsTextVisible = bagNames.map {
        it.isEmpty()
    }

    private val bagIdLiveData = MutableLiveData<Int>()
    val bagSelectedIndex = bagIdLiveData.map { bagId ->
        getBagIndexById(bagId)
    }

    // Sets spinner
    private val observableSets = MutableLiveData<List<MatchesBoxSet>>()
    val setNames = observableSets.map {
        it.map { set ->
            set.name
        }
    }
    val noSetsTextVisible = setNames.map {
        it.isEmpty()
    }
    private val setIdLiveData = MutableLiveData<Int>()
    val setSelectedIndex = setIdLiveData.switchMap { setId ->
        observableSets.map { setsList ->
            setsList.indexOfFirst { set ->
                set.id == setId
            }
        }
    }

    // Boxes spinner
    private val observableBoxes = MutableLiveData<List<MatchesBox>>()
    val boxNames = observableBoxes.map {
        it.map { box ->
            box.name
        }
    }
    val noBoxesTextVisible = boxNames.map {
        it.isEmpty()
    }
    private val boxIdLiveData = MutableLiveData<Int>()
    val boxSelectedIndex = boxIdLiveData.switchMap { boxId ->
        observableBoxes.map { boxesList ->
            boxesList.indexOfFirst { box ->
                box.id == boxId
            }
        }
    }

    fun start(boxId: Int, componentId: Int) {
        if (name.value == null) {
            matchesBoxId = boxId
            viewModelScope.launch {
                unpackRadioComponent(componentId)
                initBags()
                Log.i(tag, "start called: boxId = $boxId")

                updateSpinnersOnStart(boxId)
            }
        }
    }

    private suspend fun updateSpinnersOnStart(
        boxId: Int
    ) {
        if (boxId != NO_ID_SET) {
            updateSpinnersByBoxId(boxId)
        } else {
            updateSpinnersByBagId(getFirstBagId())
        }
    }

    private fun getFirstBagId(): Int {
        return if (bags.isNotEmpty()) {
            bags[FIRST_INDEX].id
        } else {
            NO_ID_SET
        }
    }

    private suspend fun initBags() {
        bags = dataSource.getBags()
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
        matchesBoxId = observableBoxes.value?.get(index)?.id ?: NO_ID_SET
    }

    fun setSelected(newIndex: Int) {
        //Log.i(LOG_TAG, "set selected input: index = ${newIndex}, setSelectedIndex.value = ${setSelectedIndex.value}")
        if (newIndex != setSelectedIndex.value) {
            viewModelScope.launch {
                updateSpinnersBySetId(getSetIdByIndex(newIndex))
            }
        }
    }

    private fun getSetIdByIndex(newIndex: Int): Int {
        return observableSets.value?.get(newIndex)?.id ?: NO_ID_SET
    }

    fun bagSelected(newIndex: Int) {
        Log.i(tag, "bag selected called: newIndex = $newIndex, currentSelectedIndex = $currentBagSelectedIndex")
        if (newIndex != currentBagSelectedIndex) {
            currentBagSelectedIndex = newIndex
            viewModelScope.launch {
                updateSpinnersByBagId(getBagIdByIndex(newIndex))
            }
        }
    }

    private fun getBagIdByIndex(newIndex: Int): Int {
        return observableBags.value?.get(newIndex)?.id ?: NO_ID_SET
    }

    private fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val id = insertRadioComponentToDb(name, quantity)
            insertHistory(id.toInt(), quantity)
            callAddItemEvent()
        }
    }

    private suspend fun callAddItemEvent() {
        val box = dataSource.getMatchesBoxById(matchesBoxId)
        box?.let {
            _addItemEvent.value = Event(it)
        }
    }

    private suspend fun insertRadioComponentToDb(
        name: String,
        quantity: Int
    ): Long {
        val component = RadioComponent(
            name = name,
            quantity = quantity,
            matchesBoxId = matchesBoxId,
            isBuy = isBuy.value!!
        )
        return dataSource.insertRadioComponent(component)
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

    private suspend fun updateSpinnersBySetId(setId: Int) {
        observableBoxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        setMatchesBoxIdToFirstElement()
        setIdLiveData.value = setId
        boxIdLiveData.value = matchesBoxId
    }

    private fun setMatchesBoxIdToFirstElement() {
        matchesBoxId = if (observableBoxes.value?.isNotEmpty() == true) {
            observableBoxes.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        }
    }

    private suspend fun updateSpinnersByBoxId(boxId: Int) {
        val box = dataSource.getMatchesBoxById(boxId)
        setBoxTitleForDeleteEvent(box)
        val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId ?: NO_ID_SET)
        val bagId = set?.bagId ?: NO_ID_SET
        val setId = set?.id ?: NO_ID_SET
        observableSets.value = dataSource.getMatchesBoxSetsByBagId(bagId)
        observableBoxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        updateBagsSpinner(bagId)
        bagIdLiveData.value = bagId
        setIdLiveData.value = setId
        boxIdLiveData.value = boxId
    }

    private suspend fun updateSpinnersByBagId(bagId: Int) {
        observableSets.value = dataSource.getMatchesBoxSetsByBagId(bagId)
        val firstSetId = getFirstSetId()
        observableBoxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(firstSetId)
        setMatchesBoxIdToFirstElement()
        updateBagsSpinner(bagId)
        bagIdLiveData.value = bagId
        setIdLiveData.value = firstSetId
        boxIdLiveData.value = matchesBoxId
    }

    private fun updateBagsSpinner(bagId: Int) {
        currentBagSelectedIndex = getBagIndexById(bagId)?: NO_INDEX_SET
        Log.i(tag, "currentBagSelectedIndex = $currentBagSelectedIndex")
        observableBags.value = bags
    }

    private fun getBagIndexById(bagId: Int?): Int? {
        return bags.indexOfFirst { bag ->
            bag.id == bagId
        }
    }

    private fun getFirstSetId(): Int {
        return if (observableSets.value?.isNotEmpty() == true) {
            observableSets.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        }
    }

    private fun setBoxTitleForDeleteEvent(box: MatchesBox?) {
        boxTitle = box?.name ?: ""
    }

    private suspend fun insertHistory(componentId: Int, quantity: Int) {
        val history = History(
            componentId = componentId,
            quantity = quantity
        )
        dataSource.insertHistory(history)
    }
}
