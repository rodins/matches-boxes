package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class MatchesBoxSetsListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val bagId = MutableLiveData<Int>()
    val setsList = bagId.switchMap{ id ->
        liveData{
            emit(dataSource.getDisplayQuantityListByBagId(id))
        }
    }

    val isNoSetsTextVisible = Transformations.map(setsList) { list ->
        list.isEmpty()
    }

    private val _addSetEvent = MutableLiveData<Event<Unit>>()
    val addSetEvent: LiveData<Event<Unit>>
        get() = _addSetEvent

    private val _selectSetEvent = MutableLiveData<Event<MatchesBoxSet>>()
    val selectSetEvent: LiveData<Event<MatchesBoxSet>>
        get() = _selectSetEvent

    fun startSet(id: Int) {
        bagId.value = id
    }

    fun addSet() {
        callAddEvent()
    }

    private fun callAddEvent() {
        _addSetEvent.value = Event(Unit)
    }

    fun selectSet(id: Int) {
        viewModelScope.launch {
            val set = dataSource.getMatchesBoxSetById(id)
            callSelectEvent(set)
        }
    }

    private fun callSelectEvent(set: MatchesBoxSet?) {
        set?.let {
            _selectSetEvent.value = Event(it)
        }
    }
}

class MatchesBoxSetsListViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MatchesBoxSetsListViewModel::class.java)) {
            return MatchesBoxSetsListViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No ViewModel class found.")
        }
    }

}