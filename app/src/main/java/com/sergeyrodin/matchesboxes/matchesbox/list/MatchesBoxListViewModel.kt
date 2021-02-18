package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesBoxListViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource): ViewModel() {

    private val matchesBoxSetId = MutableLiveData<Int>()
    val boxesList = matchesBoxSetId.switchMap{ setId ->
        liveData{
            emit(dataSource.getDisplayQuantityListBySetId(setId))
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

    fun startBox(id: Int) {
        matchesBoxSetId.value = id
    }

    fun addBox() {
        callAddEvent()
    }

    private fun callAddEvent() {
        _addBoxEvent.value = Event(Unit)
    }

    fun selectBox(id: Int) {
        viewModelScope.launch {
            callSelectEvent(id)
        }
    }

    private suspend fun callSelectEvent(id: Int) {
        val box = dataSource.getMatchesBoxById(id)
        _selectBoxEvent.value = Event(box!!)
    }
}
