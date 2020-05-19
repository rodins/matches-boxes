package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class BagsListViewModel(dataSource: RadioComponentsDataSource) : ViewModel(){

    val bagsList = dataSource.getBags()

    val isNoItemsTextVisible = Transformations.map(bagsList) {
        it.isEmpty()
    }

}