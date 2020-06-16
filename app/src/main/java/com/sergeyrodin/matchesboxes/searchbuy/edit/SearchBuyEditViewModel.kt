package com.sergeyrodin.matchesboxes.searchbuy.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var component: RadioComponent? = null

    fun start(componentId: Int) {
        viewModelScope.launch {
            component = dataSource.getRadioComponentById(componentId)
            _componentName.value = component?.name?:""

            val box = dataSource.getMatchesBoxById(component?.matchesBoxId!!)
            _boxName.value = box?.name?:""

            val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId!!)
            _matchesBoxSetName.value = set?.name?:""

            val bag = dataSource.getBagById(set?.bagId!!)
            _bagName.value = bag?.name?:""
        }
    }
}