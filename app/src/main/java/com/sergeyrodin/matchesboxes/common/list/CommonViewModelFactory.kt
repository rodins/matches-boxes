package com.sergeyrodin.matchesboxes.common.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import java.lang.IllegalArgumentException

class CommonViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CommonViewModel::class.java)) {
            return CommonViewModel(dataSource) as T
        }else{
            throw IllegalArgumentException("No ViewModel class found")
        }
    }
}