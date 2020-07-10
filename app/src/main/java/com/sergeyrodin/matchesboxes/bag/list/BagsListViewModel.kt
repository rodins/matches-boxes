package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class BagsListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    val bagsList = dataSource.getBagsDisplayQuantityList()

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
        _addBagEvent.value = Event(Unit)
    }

    fun selectBag(id: Int) {
        viewModelScope.launch {
            val bag = dataSource.getBagById(id)
            bag?.let{
                _selectBagEvent.value = Event(bag)
            }
        }
    }
}

class BagsListViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        if(modelClass.isAssignableFrom(BagsListViewModel::class.java)) {
            return BagsListViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No ViewModel class found.")
        }
    }

}