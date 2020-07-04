package com.sergeyrodin.matchesboxes.common.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*

class CommonViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    // Bags

    private val components = dataSource.getRadioComponents()
    val bagsList = components.switchMap {radioComponents ->
        liveData {
             emit(getBagsDisplayQuantityList(radioComponents))
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

    private lateinit var boxes: List<MatchesBox>

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

    private val _selectBoxEvent = MutableLiveData<Event<MatchesBox>>()
    val selectBoxEvent: LiveData<Event<MatchesBox>>
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
        boxes.find { box ->
            box.id == id
        }?.also { box ->
            _selectBoxEvent.value = Event(box)
        }
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

    // Components

    fun startComponent(id: Int) {
        boxId.value = id
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }

    // Bags
    private suspend fun getBagsDisplayQuantityList(radioComponents: List<RadioComponent>): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        for(bag in dataSource.getBags()) {
            var componentsQuantity = 0
            for(set in dataSource.getMatchesBoxSetsByBagId(bag.id)){
                for(box in dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                    val boxComponents = radioComponents.filter { component ->
                        component.matchesBoxId == box.id
                    }
                    for(component in boxComponents) {
                        componentsQuantity += component.quantity
                    }
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
                for(component in dataSource.getRadioComponentsByMatchesBoxId(box.id)){
                    componentsQuantity += component.quantity
                }
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
        boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        for (box in boxes) {
            var componentsQuantity = 0
            for(component in dataSource.getRadioComponentsByMatchesBoxId(box.id)) {
                componentsQuantity += component.quantity
            }
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

    // Testing

    @VisibleForTesting
    suspend fun initBoxes(setId: Int) {
        boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
    }
}