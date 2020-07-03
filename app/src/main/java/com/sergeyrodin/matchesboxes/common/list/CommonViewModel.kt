package com.sergeyrodin.matchesboxes.common.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.DisplayQuantity
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class CommonViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private lateinit var bags: List<Bag>

    private val components = dataSource.getRadioComponents()
    val bagsList = components.switchMap {radioComponents ->
        liveData {
             emit(getBagsDisplayQuantityList(radioComponents))
        }
    }

    private suspend fun getBagsDisplayQuantityList(radioComponents: List<RadioComponent>): List<DisplayQuantity> {
        bags = dataSource.getBags()
        val output = mutableListOf<DisplayQuantity>()
        for(bag in bags) {
            var componentsQuantity = 0
            for(set in dataSource.getMatchesBoxSetsByBagId(bag.id)){
                for(box in dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)) {
                    val boxComponents = radioComponents.filter { component ->
                        component.matchesBoxId == box.id
                    }
                    for(component in boxComponents) {
                        componentsQuantity += component.quantity
                    }
                }
            }
            val displayQuantity =
                DisplayQuantity(
                    bag.id,
                    bag.name,
                    componentsQuantity.toString()
                )
            output.add(displayQuantity)
        }
        return output
    }
}