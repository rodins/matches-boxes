package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.util.Log
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*
import kotlinx.coroutines.launch

const val NO_ID_SET = -1
private const val NO_INDEX_SET = -1
private const val FIRST_INDEX = 0

class RadioComponentManipulatorViewModel(private val dataSource: RadioComponentsDataSource) :
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

    private val selectedBagId = MutableLiveData<Int>()
    val bagSelectedIndex = selectedBagId.map { bagId ->
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
    private val selectedSetId = MutableLiveData<Int>()
    val setSelectedIndex = selectedSetId.switchMap { setId ->
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
    private val selectedBoxId = MutableLiveData<Int>()
    val boxSelectedIndex = selectedBoxId.switchMap { boxId ->
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
                Log.i(tag, "start called: boxId = $boxId")
                updateSpinnersOnStart(boxId)
            }
        }
    }

    private suspend fun unpackRadioComponent(componentId: Int) {
        radioComponent = dataSource.getRadioComponentById(componentId)
        name.value = radioComponent?.name ?: ""
        quantity.value = (radioComponent?.quantity ?: "").toString()
        isBuy.value = radioComponent?.isBuy ?: false
    }

    private suspend fun updateSpinnersOnStart(
        boxId: Int
    ) {
        initBags()
        if (boxId != NO_ID_SET) {
            updateSpinnersByBoxId(boxId)
        } else {
            updateSpinnersByBagId(getFirstBagId())
        }
    }

    private suspend fun initBags() {
        bags = dataSource.getBags()
    }

    private suspend fun updateSpinnersByBoxId(boxId: Int) {
        val box = getMatchesBoxFromDbById(boxId)
        setBoxTitleForDeleteEvent(box)
        val set = getMatchesBoxSetParentOfBox(box)
        val bagId = set?.bagId ?: NO_ID_SET
        val setId = set?.id ?: NO_ID_SET
        getSetsFromDb(bagId)
        getMatchesBoxesFromDb(setId)
        updateBagsSpinner(bagId)
        updateSelectedIds(bagId, setId, boxId)
    }

    private suspend fun getMatchesBoxFromDbById(boxId: Int): MatchesBox? {
        return dataSource.getMatchesBoxById(boxId)
    }

    private fun setBoxTitleForDeleteEvent(box: MatchesBox?) {
        boxTitle = box?.name ?: ""
    }

    private suspend fun getMatchesBoxSetParentOfBox(box: MatchesBox?): MatchesBoxSet? {
        val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId ?: NO_ID_SET)
        return set
    }

    private suspend fun updateSpinnersByBagId(bagId: Int) {
        getSetsFromDb(bagId)
        val firstSetId = getFirstSetId()
        getMatchesBoxesFromDb(firstSetId)
        setMatchesBoxIdToFirstElement()
        updateBagsSpinner(bagId)
        updateSelectedIds(bagId, firstSetId, matchesBoxId)
    }

    private fun updateSelectedIds(bagId: Int, setId: Int, boxId: Int) {
        selectedBagId.value = bagId
        selectedSetId.value = setId
        selectedBoxId.value = boxId
    }

    private suspend fun getSetsFromDb(bagId: Int) {
        observableSets.value = dataSource.getMatchesBoxSetsByBagId(bagId)
    }

    private fun getFirstBagId(): Int {
        return if (bags.isNotEmpty()) {
            bags[FIRST_INDEX].id
        } else {
            NO_ID_SET
        }
    }

    fun saveItem() {
        if (name.value?.trim() != "" && matchesBoxId != NO_ID_SET) {
            val componentsQuantity = quantity.value?.toIntOrNull() ?: 0
            if (componentsQuantity >= 0) {
                if (radioComponent == null) {
                    addItem(name.value!!, componentsQuantity)
                } else {
                    updateItem(name.value!!, componentsQuantity)
                }
            }
        } else {
            _errorEvent.value = Event(Unit)
        }
    }

    private fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val id = insertRadioComponentToDb(name, quantity)
            insertHistory(id.toInt(), quantity)
            callAddItemEvent()
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

    private suspend fun insertHistory(componentId: Int, quantity: Int) {
        val history = History(
            componentId = componentId,
            quantity = quantity
        )
        dataSource.insertHistory(history)
    }

    private suspend fun callAddItemEvent() {
        val box = getMatchesBoxFromDbById(matchesBoxId)
        box?.let {
            _addItemEvent.value = Event(it)
        }
    }

    private fun updateItem(name: String, quantity: Int) {
        viewModelScope.launch {
            updateRadioComponentInDb(name, quantity)
            insertHistory(radioComponent!!.id, radioComponent!!.quantity)
            callUpdateItemEvent()
        }
    }

    private suspend fun updateRadioComponentInDb(name: String, quantity: Int) {
        radioComponent?.name = name
        radioComponent?.quantity = quantity
        radioComponent?.matchesBoxId = matchesBoxId
        radioComponent?.isBuy = isBuy.value!!
        dataSource.updateRadioComponent(radioComponent!!)
    }

    private suspend fun callUpdateItemEvent() {
        val box = getMatchesBoxFromDbById(matchesBoxId)
        box?.let {
            _updateItemEvent.value = Event(box)
        }
    }



    fun deleteItem() {
        viewModelScope.launch {
            deleteRadioComponentFromDb()
            callDeleteItemEvent()
        }
    }

    private suspend fun deleteRadioComponentFromDb() {
        dataSource.deleteRadioComponent(radioComponent!!)
    }

    private fun callDeleteItemEvent() {
        _deleteItemEvent.value = Event(boxTitle)
    }

    fun quantityPlus() {
        val numberOfComponents = convertTextQuantityToNumber()
        quantity.value = numberOfComponents.plus(1).toString()
    }

    fun quantityMinus() {
        val numberOfComponents = convertTextQuantityToNumber()
        quantity.value = numberOfComponents.minus(1).toString()
    }

    private fun convertTextQuantityToNumber(): Int {
        return quantity.value?.toIntOrNull() ?: 0
    }

    fun boxSelected(index: Int) {
        convertIndexToBoxId(index)
    }

    private fun convertIndexToBoxId(index: Int) {
        matchesBoxId = observableBoxes.value?.get(index)?.id ?: NO_ID_SET
    }

    fun setSelected(newIndex: Int) {
        if (newIndex != setSelectedIndex.value) {
            viewModelScope.launch {
                updateSpinnersBySetId(getSetIdByIndex(newIndex))
            }
        }
    }

    private suspend fun updateSpinnersBySetId(setId: Int) {
        getMatchesBoxesFromDb(setId)
        setMatchesBoxIdToFirstElement()
        updateSelectedIds(setId)
    }

    private suspend fun getMatchesBoxesFromDb(setId: Int) {
        observableBoxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
    }

    private fun setMatchesBoxIdToFirstElement() {
        matchesBoxId = if (observableBoxes.value?.isNotEmpty() == true) {
            observableBoxes.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        }
    }

    private fun updateSelectedIds(setId: Int) {
        selectedSetId.value = setId
        selectedBoxId.value = matchesBoxId
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
}
