package com.sergeyrodin.matchesboxes.component.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class RadioComponentsListViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _items = MutableLiveData<List<RadioComponent>>()
    val items: LiveData<List<RadioComponent>>
        get() = _items

    val noItemsTextVisible = Transformations.map(items) {
        it.isEmpty()
    }

    fun start(boxId: Int) {
        viewModelScope.launch {
            _items.value = dataSource.getRadioComponentsByMatchesBoxId(boxId)
        }
    }
}