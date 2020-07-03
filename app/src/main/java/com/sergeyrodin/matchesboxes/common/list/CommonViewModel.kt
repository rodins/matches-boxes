package com.sergeyrodin.matchesboxes.common.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.DisplayQuantity
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class CommonViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
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
}