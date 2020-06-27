package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DisplayQuantity
import com.sergeyrodin.matchesboxes.util.getMatchesBoxesQuantityList
import kotlinx.coroutines.launch

class MatchesBoxListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val matchesBoxSetId = MutableLiveData<Int>()
    val matchesBoxes = matchesBoxSetId.switchMap{ setId ->
        liveData{
            val items = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
            emit(getMatchesBoxesQuantityList(dataSource, items))
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

    private val _setTitle = MutableLiveData<String>()
    val setTitle: MutableLiveData<String>
        get() = _setTitle

    fun start(setId: Int) {
        matchesBoxSetId.value = setId
        viewModelScope.launch {
            val set = dataSource.getMatchesBoxSetById(setId)
            _setTitle.value = set?.name
        }
    }

    fun addMatchesBox() {
        _addMatchesBoxEvent.value = Event(Unit)
    }

    fun selectMatchesBox(id: Int) {
        _selectMatchesBoxEvent.value = Event(id)
    }

}