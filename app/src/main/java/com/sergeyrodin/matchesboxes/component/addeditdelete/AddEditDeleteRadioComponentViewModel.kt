package com.sergeyrodin.matchesboxes.component.addeditdelete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class AddEditDeleteRadioComponentViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _quantity = MutableLiveData<String>()
    val quantity: LiveData<String>
        get() = _quantity

    private var radioComponent: RadioComponent? = null

    fun start(boxId: Int, componentId: Int){
        viewModelScope.launch {
            radioComponent = dataSource.getRadioComponentById(componentId)
        }
        _name.value = radioComponent?.name?:""
        _quantity.value = (radioComponent?.quantity?:0).toString()

    }

}