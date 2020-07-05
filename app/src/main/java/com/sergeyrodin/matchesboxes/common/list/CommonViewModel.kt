package com.sergeyrodin.matchesboxes.common.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.DisplayQuantity
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class CommonViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    // Bags

    private val componentsCount = dataSource.getRadioComponentsCount()
    val bagsList = componentsCount.switchMap {
        liveData {
             emit(getBagsDisplayQuantityList())
        }
    }

    val isNoBagsTextVisible = Transformations.map(bagsList) {list ->
        list.isEmpty()
    }

    private val _addBagEvent = MutableLiveData<Event<Unit>>()
    val addBagEvent: LiveData<Event<Unit>>
        get() = _addBagEvent

    private val _selectBagEvent = MutableLiveData<Event<Int>>()
    val selectBagEvent: LiveData<Event<Int>>
        get() = _selectBagEvent

    fun addBag() {
        _addBagEvent.value = Event(Unit)
    }

    fun selectBag(id: Int) {
        _selectBagEvent.value = Event(id)
    }

    // Sets
    private val bagId = MutableLiveData<Int>()
    val setsList = bagId.switchMap{ id ->
        liveData{
            emit(getMatchesBoxSetQuantityList(id))
        }
    }

    val isNoSetsTextVisible = Transformations.map(setsList) { list ->
        list.isEmpty()
    }

    private val _addSetEvent = MutableLiveData<Event<Unit>>()
    val addSetEvent: LiveData<Event<Unit>>
        get() = _addSetEvent

    private val _selectSetEvent = MutableLiveData<Event<Int>>()
    val selectSetEvent: LiveData<Event<Int>>
        get() = _selectSetEvent

    val bagTitle = bagId.switchMap { id ->
        liveData<String>{
            val bag = dataSource.getBagById(id)
            emit(bag?.name?:"")
        }
    }

    fun startSet(id: Int) {
        bagId.value = id
    }

    fun addSet() {
        _addSetEvent.value = Event(Unit)
    }

    fun selectSet(id: Int) {
        _selectSetEvent.value = Event(id)
    }

    // Boxes

    private val matchesBoxSetId = MutableLiveData<Int>()
    val boxesList = matchesBoxSetId.switchMap{ setId ->
        liveData{
            emit(getMatchesBoxesQuantityList(setId))
        }
    }

    val isNoBoxesTextVisible = Transformations.map(boxesList) {
        it.isEmpty()
    }

    private val _addBoxEvent = MutableLiveData<Event<Unit>>()
    val addBoxEvent: LiveData<Event<Unit>>
        get() = _addBoxEvent

    private val _selectBoxEvent = MutableLiveData<Event<Int>>()
    val selectBoxEvent: LiveData<Event<Int>>
        get() = _selectBoxEvent

    val matchesBoxSetTitle = matchesBoxSetId.switchMap { id ->
        liveData<String>{
            val set = dataSource.getMatchesBoxSetById(id)
            emit(set?.name?:"")
        }
    }

    fun startBox(id: Int) {
        matchesBoxSetId.value = id
    }

    fun addBox() {
        _addBoxEvent.value = Event(Unit)
    }

    fun selectBox(id: Int) {
        _selectBoxEvent.value = Event(id)
    }

    // Components

    private val boxId = MutableLiveData<Int>()
    val componentsList = boxId.switchMap{
        liveData{
            emit(dataSource.getRadioComponentsByMatchesBoxId(it))
        }
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    val noComponentsTextVisible = Transformations.map(componentsList) { list ->
        list.isEmpty()
    }

    val boxTitle = boxId.switchMap { id ->
        liveData<String>{
            val box = dataSource.getMatchesBoxById(id)
            emit(box?.name?:"")
        }
    }

    fun startComponent(id: Int) {
        boxId.value = id
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }

    // Bags
    private suspend fun getBagsDisplayQuantityList(): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        for(bag in dataSource.getBags()) {
            var componentsQuantity = 0
            for(set in dataSource.getMatchesBoxSetsByBagId(bag.id)){
                for(box in dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                    componentsQuantity += dataSource.getRadioComponentsSumQuantityByMatchesBoxId(box.id)
                }
            }
            val displayQuantity =
                DisplayQuantity(
                    bag.id,
                    bag.name,
                    componentsQuantity.toString()
                )
            output.add(displayQuantity)
        }
        return output
    }

    // Sets
    private suspend fun getMatchesBoxSetQuantityList(bagId: Int): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        for (set in dataSource.getMatchesBoxSetsByBagId(bagId)) {
            var componentsQuantity = 0
            for(box in dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                componentsQuantity += dataSource.getRadioComponentsSumQuantityByMatchesBoxId(box.id)
            }
            val setQuantity =
                DisplayQuantity(
                    set.id,
                    set.name,
                    componentsQuantity.toString()
                )
            output.add(setQuantity)
        }
        return output
    }

    // Boxes

    private suspend fun getMatchesBoxesQuantityList(setId: Int): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        for (box in dataSource.getMatchesBoxesByMatchesBoxSetId(setId)) {
            val componentsQuantity = dataSource.getRadioComponentsSumQuantityByMatchesBoxId(box.id)
            val boxQuantity =
                DisplayQuantity(
                    box.id,
                    box.name,
                    componentsQuantity.toString()
                )
            output.add(boxQuantity)
        }
        return output
    }
}