package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class BagsListViewModel(dataSource: RadioComponentsDataSource) : ViewModel(){

    val bagsList = dataSource.getBags()

    val isNoItemsTextVisible = Transformations.map(bagsList) {
        it.isEmpty()
    }

    private val _addItemEvent = MutableLiveData<Event<Unit>>()
    val addItemEvent: LiveData<Event<Unit>>
        get() = _addItemEvent

    private val _selectItemEvent = MutableLiveData<Event<Int>>()
    val selectItemEvent: LiveData<Event<Int>>
        get() = _selectItemEvent

    fun addItem() {
        _addItemEvent.value = Event(Unit)
    }

    fun selectItem(id: Int) {
        _selectItemEvent.value = Event(id)
    }

}