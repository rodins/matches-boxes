package com.sergeyrodin.matchesboxes.searchbuy.edit

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class SearchBuyEditViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _componentName = MutableLiveData<String>()
    val componentName: LiveData<String>
        get() = _componentName

    private val _boxName = MutableLiveData<String>()
    val boxName: LiveData<String>
        get() = _boxName

    private val _matchesBoxSetName = MutableLiveData<String>()
    val matchesBoxSetName: LiveData<String>
        get() = _matchesBoxSetName

    private val _bagName = MutableLiveData<String>()
    val bagName: LiveData<String>
        get() = _bagName

    val quantity = MutableLiveData<String>()

    val minusEnabled = Transformations.map(quantity) {
        it.toInt() > 0
    }

    private val _saveItemEvent = MutableLiveData<Event<Unit>>()
    val saveItemEvent: LiveData<Event<Unit>>
        get() = _saveItemEvent

    private var component: RadioComponent? = null

    fun start(componentId: Int) {
        viewModelScope.launch {
            component = dataSource.getRadioComponentById(componentId)
            _componentName.value = component?.name?:""
            quantity.value = component?.quantity.toString()

            val box = dataSource.getMatchesBoxById(component?.matchesBoxId!!)
            _boxName.value = box?.name?:""

            val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId!!)
            _matchesBoxSetName.value = set?.name?:""

            val bag = dataSource.getBagById(set?.bagId!!)
            _bagName.value = bag?.name?:""
        }
    }

    fun quantityPlus() {
        val nQuantity = quantity.value?.toInt()?.plus(1)?:0
        quantity.value = nQuantity.toString()
    }

    fun quantityMinus() {
        val nQuantity = quantity.value?.toInt()?.minus(1)?:0
        quantity.value = nQuantity.toString()
    }

    fun saveItem() {
        viewModelScope.launch {
            component?.quantity = quantity.value?.toInt()!!
            dataSource.updateRadioComponent(component!!)
            _saveItemEvent.value = Event(Unit)
        }
    }
}