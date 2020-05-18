package com.sergeyrodin.matchesboxes.bag.list

import androidx.lifecycle.Transformations
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class BagsListViewModel(dataSource: RadioComponentsDataSource) {

    val bagsList = dataSource.getBags()

    val isNoItemsTextVisible = Transformations.map(bagsList) {
        it.size == 0
    }

}