package com.sergeyrodin.matchesboxes.bag.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.DisplayQuantity
import kotlinx.coroutines.launch

class BagsListViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel(){
    private lateinit var bags: List<Bag>
    private val components = dataSource.getRadioComponents()
    val bagsList = components.switchMap {
        liveData {
            //TODO: make it a function
            bags = dataSource.getBags()
            val output = mutableListOf<DisplayQuantity>()
            for(bag in bags) {
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

    private val _selectItemEvent = MutableLiveData<Event<Bag>>()
    val selectItemEvent: LiveData<Event<Bag>>
        get() = _selectItemEvent

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        bags.find {
            it.id == id
        }?.also {
            _selectItemEvent.value = Event(it)
        }
    }

}