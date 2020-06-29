package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.DisplayQuantity
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet

class MatchesBoxSetsListViewModel(private val radioComponentsDataSource: RadioComponentsDataSource): ViewModel() {
    private lateinit var sets: List<MatchesBoxSet>

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

    private val _selectItemEvent = MutableLiveData<Event<MatchesBoxSet>>()
    val selectItemEvent: LiveData<Event<MatchesBoxSet>>
        get() = _selectItemEvent

    private val _bagTitle = MutableLiveData<String>()
    val bagTitle: LiveData<String>
        get() = _bagTitle


    fun start(bag: Bag) {
        bagId.value = bag.id
        _bagTitle.value = bag.name
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        sets.find { set ->
            set.id == id
        }?.also { set ->
            _selectItemEvent.value = Event(set)
        }
    }

    private suspend fun getMatchesBoxSetQuantityList(bagId: Int): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        sets = radioComponentsDataSource.getMatchesBoxSetsByBagId(bagId)
        for (set in sets) {
            var componentsQuantity = 0
            for(box in radioComponentsDataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                for(component in radioComponentsDataSource.getRadioComponentsByMatchesBoxId(box.id)){
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
}