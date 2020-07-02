package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.DisplayQuantity
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet

class MatchesBoxListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private lateinit var boxes: List<MatchesBox>
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

    private val _selectMatchesBoxEvent = MutableLiveData<Event<MatchesBox>>()
    val selectMatchesBoxEvent: LiveData<Event<MatchesBox>>
        get() = _selectMatchesBoxEvent

    fun start(id: Int) {
        matchesBoxSetId.value = id
    }

    fun addMatchesBox() {
        _addMatchesBoxEvent.value = Event(Unit)
    }

    fun selectMatchesBox(id: Int) {
        boxes.find { box ->
            box.id == id
        }?.also { box ->
            _selectMatchesBoxEvent.value = Event(box)
        }
    }

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

    @VisibleForTesting
    suspend fun initBoxes(setId: Int) {
        boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
    }
}