package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BagsListViewModel @Inject constructor(
    private val dataSource: RadioComponentsDataSource
) : ViewModel() {
    val bagsList = dataSource.getBagsQuantityPresentationList()

    val isNoBagsTextVisible = Transformations.map(bagsList) { list ->
        list.isEmpty()
    }

    private val _addBagEvent = MutableLiveData<Event<Unit>>()
    val addBagEvent: LiveData<Event<Unit>>
        get() = _addBagEvent

    private val _selectBagEvent = MutableLiveData<Event<Bag>>()
    val selectBagEvent: LiveData<Event<Bag>>
        get() = _selectBagEvent

    fun addBag() {
        callAddBagEvent()
    }

    private fun callAddBagEvent() {
        _addBagEvent.value = Event(Unit)
    }

    fun selectBag(id: Int) {
        viewModelScope.launch {
            val bag = getBagById(id)
            callSelectBagEvent(bag)
        }
    }

    private suspend fun getBagById(id: Int): Bag? {
        return dataSource.getBagById(id)
    }

    private fun callSelectBagEvent(bag: Bag?) {
        bag?.let {
            _selectBagEvent.value = Event(bag)
        }
    }
}