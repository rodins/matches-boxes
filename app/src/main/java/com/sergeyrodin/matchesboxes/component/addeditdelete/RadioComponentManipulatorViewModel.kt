package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*
import kotlinx.coroutines.launch

const val NO_ID_SET = -1
const val NO_INDEX_SET = -1
const val FIRST_INDEX = 0

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

    private val _savingErrorEvent = MutableLiveData<Event<Unit>>()
    val savingErrorEvent: LiveData<Event<Unit>>
        get() = _savingErrorEvent

    val minusEnabled = Transformations.map(quantity) {
        it.toIntOrNull() ?: 0 != 0
    }

    private val spinnersUpdater = SpinnersUpdater(dataSource)

    private var radioComponent: RadioComponent? = null

    val bagNames = spinnersUpdater.bagNames
    val noBagsTextVisible = spinnersUpdater.noBagsTextVisible
    val bagSelectedIndex = spinnersUpdater.bagSelectedIndex

    val setNames = spinnersUpdater.setNames
    val noSetsTextVisible = spinnersUpdater.noSetsTextVisible
    val setSelectedIndex = spinnersUpdater.setSelectedIndex

    val boxNames = spinnersUpdater.boxNames
    val noBoxesTextVisible = spinnersUpdater.noBoxesTextVisible
    val boxSelectedIndex = spinnersUpdater.boxSelectedIndex

    fun start(boxId: Int, componentId: Int) {
        if (name.value == null) {
            viewModelScope.launch {
                unpackRadioComponent(componentId)
                spinnersUpdater.start(boxId)
            }
        }
    }

    private suspend fun unpackRadioComponent(componentId: Int) {
        radioComponent = dataSource.getRadioComponentById(componentId)
        name.value = radioComponent?.name ?: ""
        quantity.value = (radioComponent?.quantity ?: "").toString()
        isBuy.value = radioComponent?.isBuy ?: false
    }

    fun saveItem() {
        if (name.value?.trim() != "" && spinnersUpdater.matchesBoxId != NO_ID_SET) {
            saveItemIfQuantityIsNotNegative(getComponentsQuantity())
        } else {
            callSavingErrorEvent()
        }
    }

    private fun saveItemIfQuantityIsNotNegative(componentsQuantity: Int) {
        if (componentsQuantity >= 0) {
            addOrUpdateRadioComponent(componentsQuantity)
        } else {
            callSavingErrorEvent()
        }
    }

    private fun getComponentsQuantity() = quantity.value?.toIntOrNull() ?: 0

    private fun addOrUpdateRadioComponent(componentsQuantity: Int) {
        if (radioComponent == null) {
            addItem(name.value!!, componentsQuantity)
        } else {
            updateItem(name.value!!, componentsQuantity)
        }
    }

    private fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val id = insertRadioComponentToDbAndReturnId(name, quantity)
            insertHistory(id.toInt(), quantity)
            callAddItemEvent()
        }
    }

    private suspend fun insertRadioComponentToDbAndReturnId(
        name: String,
        quantity: Int
    ): Long {
        val component = RadioComponent(
            name = name,
            quantity = quantity,
            matchesBoxId = spinnersUpdater.matchesBoxId,
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
        val box = getMatchesBoxFromDbByMatchesBoxIdProperty()
        box?.let {
            _addItemEvent.value = Event(it)
        }
    }

    private suspend fun getMatchesBoxFromDbByMatchesBoxIdProperty(): MatchesBox? {
        return dataSource.getMatchesBoxById(spinnersUpdater.matchesBoxId)
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
        radioComponent?.matchesBoxId = spinnersUpdater.matchesBoxId
        radioComponent?.isBuy = isBuy.value!!
        dataSource.updateRadioComponent(radioComponent!!)
    }

    private suspend fun callUpdateItemEvent() {
        val box = getMatchesBoxFromDbByMatchesBoxIdProperty()
        box?.let {
            _updateItemEvent.value = Event(box)
        }
    }

    private fun callSavingErrorEvent() {
        _savingErrorEvent.value = Event(Unit)
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

    private suspend fun callDeleteItemEvent() {
        val boxTitle = getMatchesBoxTitleFromMatchesBoxId()
        _deleteItemEvent.value = Event(boxTitle)
    }

    private suspend fun getMatchesBoxTitleFromMatchesBoxId(): String {
        val box = getMatchesBoxFromDbByMatchesBoxIdProperty()
        return box?.name ?: ""
    }

    fun quantityPlus() {
        val numberOfComponents = convertTextQuantityToNumber()
        quantity.value = numberOfComponents.plus(1).toString()
    }

    private fun convertTextQuantityToNumber(): Int {
        return quantity.value?.toIntOrNull() ?: 0
    }

    fun quantityMinus() {
        val numberOfComponents = convertTextQuantityToNumber()
        quantity.value = numberOfComponents.minus(1).toString()
    }

    fun boxSelected(newIndex: Int){
        spinnersUpdater.boxSelected(newIndex)
    }

    fun setOfBoxesSelected(newIndex: Int) {
        viewModelScope.launch {
            spinnersUpdater.setSelected(newIndex)
        }
    }

    fun bagSelected(newIndex: Int) {
        viewModelScope.launch {
            spinnersUpdater.bagSelected(newIndex)
        }
    }
}
