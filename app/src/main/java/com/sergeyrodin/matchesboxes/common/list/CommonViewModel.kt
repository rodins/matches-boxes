package com.sergeyrodin.matchesboxes.common.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.*

class CommonViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    // Bags
    private lateinit var bags: List<Bag>

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

    private val _selectBagEvent = MutableLiveData<Event<Bag>>()
    val selectBagEvent: LiveData<Event<Bag>>
        get() = _selectBagEvent

    // Sets
    private lateinit var sets: List<MatchesBoxSet>

    private val bagId = MutableLiveData<Int>()
    val setsList = bagId.switchMap{ id ->
        liveData{
            emit(getMatchesBoxSetQuantityList(id))
        }
    }

    val isNoSetsTextVisible = Transformations.map(setsList) {
        it.isEmpty()
    }

    private val _addSetEvent = MutableLiveData<Event<Unit>>()
    val addSetEvent: LiveData<Event<Unit>>
        get() = _addSetEvent

    private val _selectSetEvent = MutableLiveData<Event<MatchesBoxSet>>()
    val selectSetEvent: LiveData<Event<MatchesBoxSet>>
        get() = _selectSetEvent


    // Bags
    fun addBag() {
        _addBagEvent.value = Event(Unit)
    }

    fun selectBag(id: Int) {
        bags.find {
            it.id == id
        }?.also {
            _selectBagEvent.value = Event(it)
        }
    }

    // Sets

    fun startSet(id: Int) {
        bagId.value = id
    }

    fun addSet() {
        _addSetEvent.value = Event(Unit)
    }

    fun selectSet(id: Int) {
        sets.find { set ->
            set.id == id
        }?.also { set ->
            _selectSetEvent.value = Event(set)
        }
    }

    // Bags
    private suspend fun getBagsDisplayQuantityList(radioComponents: List<RadioComponent>): List<DisplayQuantity> {
        bags = dataSource.getBags()
        val output = mutableListOf<DisplayQuantity>()
        for(bag in bags) {
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
        sets = dataSource.getMatchesBoxSetsByBagId(bagId)
        for (set in sets) {
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

    @VisibleForTesting
    suspend fun initSets(bagId: Int) {
        sets = dataSource.getMatchesBoxSetsByBagId(bagId)
    }

    // Boxes

}