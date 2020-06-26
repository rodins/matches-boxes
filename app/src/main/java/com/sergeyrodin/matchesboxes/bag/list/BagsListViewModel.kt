package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.util.DisplayQuantity
import com.sergeyrodin.matchesboxes.util.getBagQuantityList
import kotlinx.coroutines.launch

class BagsListViewModel(private val dataSource: RadioComponentsDataSource) : ViewModel(){

    /*val bagsList = liveData{
            emit(getBagQuantityList(dataSource))
    }*/

    private val _bagsList = MutableLiveData<List<DisplayQuantity>>()
    val bagsList: LiveData<List<DisplayQuantity>>
        get() = _bagsList

    val isNoItemsTextVisible = Transformations.map(bagsList) {
        it.isEmpty()
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _selectItemEvent = MutableLiveData<Event<Int>>()
    val selectItemEvent: LiveData<Event<Int>>
        get() = _selectItemEvent

    fun updateQuantity() {
        viewModelScope.launch {
            _bagsList.value = getBagQuantityList(dataSource)
        }
    }

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }

}