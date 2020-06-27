package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DisplayQuantity

class MatchesBoxSetsListViewModel(private val radioComponentsDataSource: RadioComponentsDataSource): ViewModel() {
    private val bagId = MutableLiveData<Int>()
    val matchesBoxSets = bagId.switchMap{
        liveData{
            emit(getMatchesBoxSetQuantityList(it))
        }
    }

    val isNoMatchesBoxSetsTextVisible = Transformations.map(matchesBoxSets) {
        it.isEmpty()
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _selectItemEvent = MutableLiveData<Event<Int>>()
    val selectItemEvent: LiveData<Event<Int>>
        get() = _selectItemEvent


    val bagTitle = bagId.switchMap {
        liveData{
            val bag = radioComponentsDataSource.getBagById(it)
            emit(bag?.name)
        }
    }


    fun start(id: Int) {
        bagId.value = id
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }

    private suspend fun getMatchesBoxSetQuantityList(bagId: Int): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        for (set in radioComponentsDataSource.getMatchesBoxSetsByBagId(bagId)) {
            var componentsQuantity = 0
            for(box in radioComponentsDataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                for(component in radioComponentsDataSource.getRadioComponentsByMatchesBoxId(box.id)){
                    componentsQuantity += component.quantity
                }
            }
            val setQuantity =
                DisplayQuantity(set.id, set.name, componentsQuantity.toString())
            output.add(setQuantity)
        }
        return output
    }
}