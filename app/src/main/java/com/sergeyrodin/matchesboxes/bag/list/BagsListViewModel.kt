package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.DisplayQuantity

class BagsListViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel(){

    private val components = dataSource.getRadioComponents()
    val bagsList = components.switchMap {
        liveData {
            val output = mutableListOf<DisplayQuantity>()
            for(bag in dataSource.getBags()) {
                var componentsQuantity = 0
                for(set in dataSource.getMatchesBoxSetsByBagId(bag.id)){
                    for(box in dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                        val boxComponents = it.filter { component ->
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
            emit(output)
        }
    }

    val isNoItemsTextVisible = Transformations.map(bagsList) {
        it.isEmpty()
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _selectItemEvent = MutableLiveData<Event<Int>>()
    val selectItemEvent: LiveData<Event<Int>>
        get() = _selectItemEvent

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }

}