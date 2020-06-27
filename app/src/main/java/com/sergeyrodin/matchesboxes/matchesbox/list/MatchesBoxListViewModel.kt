package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.DisplayQuantity

class MatchesBoxListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val matchesBoxSetId = MutableLiveData<Int>()
    val matchesBoxes = matchesBoxSetId.switchMap{ setId ->
        liveData{
            emit(getMatchesBoxesQuantityList(setId))
        }
    }

    val isNoItemsTextVisible = Transformations.map(matchesBoxes) {
        it.isEmpty()
    }

    private val _addMatchesBoxEvent = MutableLiveData<Event<Unit>>()
    val addMatchesBoxEvent: LiveData<Event<Unit>>
        get() = _addMatchesBoxEvent

    private val _selectMatchesBoxEvent = MutableLiveData<Event<Int>>()
    val selectMatchesBoxEvent: LiveData<Event<Int>>
        get() = _selectMatchesBoxEvent

    val setTitle = matchesBoxSetId.switchMap {
        liveData{
            val set = dataSource.getMatchesBoxSetById(it)
            emit(set?.name)
        }
    }

    fun start(setId: Int) {
        matchesBoxSetId.value = setId
    }

    fun addMatchesBox() {
        _addMatchesBoxEvent.value = Event(Unit)
    }

    fun selectMatchesBox(id: Int) {
        _selectMatchesBoxEvent.value = Event(id)
    }

    private suspend fun getMatchesBoxesQuantityList(setId: Int): List<DisplayQuantity> {
        val output = mutableListOf<DisplayQuantity>()
        for (box in dataSource.getMatchesBoxesByMatchesBoxSetId(setId)) {
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

}