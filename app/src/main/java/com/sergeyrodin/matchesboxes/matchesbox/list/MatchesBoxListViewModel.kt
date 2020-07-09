package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class MatchesBoxListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
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
        _addBoxEvent.value = Event(Unit)
    }

    fun selectBox(id: Int) {
        viewModelScope.launch {
            val box = dataSource.getMatchesBoxById(id)
            _selectBoxEvent.value = Event(box!!)
        }
    }
}

class MatchesBoxListViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MatchesBoxListViewModel::class.java)) {
            return MatchesBoxListViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No ViewModel class found")
        }
    }
}