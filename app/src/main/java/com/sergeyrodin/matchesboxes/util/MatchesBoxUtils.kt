package com.sergeyrodin.matchesboxes.util

import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

data class DisplayQuantity(
    val id: Int,
    val name: String,
    val componentsQuantity: String
)

suspend fun getMatchesBoxesQuantityList(dataSource: RadioComponentsDataSource, matchesBoxes: List<MatchesBox>): List<DisplayQuantity> {
    val output = mutableListOf<DisplayQuantity>()
    for (box in matchesBoxes) {
        val components = dataSource.getRadioComponentsByMatchesBoxId(box.id)
        var componentsQuantity = 0
        for(component in components) {
            componentsQuantity += component.quantity
        }
        val boxQuantity =
            DisplayQuantity(box.id, box.name, componentsQuantity.toString())
        output.add(boxQuantity)
    }
    return output
}

suspend fun getMatchesBoxSetQuantityList(dataSource: RadioComponentsDataSource, sets: List<MatchesBoxSet>): List<DisplayQuantity> {
    val output = mutableListOf<DisplayQuantity>()
    for (set in sets) {
        val boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
        var componentsQuantity = 0
        for(box in boxes) {
            val components = dataSource.getRadioComponentsByMatchesBoxId(box.id)
            for(component in components) {
                componentsQuantity += component.quantity
            }
        }
        val setQuantity =
            DisplayQuantity(set.id, set.name, componentsQuantity.toString())
        output.add(setQuantity)
    }
    return output
}

suspend fun getBagQuantityList(dataSource: RadioComponentsDataSource): List<DisplayQuantity> {
    val output = mutableListOf<DisplayQuantity>()
    val bags = dataSource.getBags()
    for(bag in bags) {
        var componentsQuantity = 0
        val sets = dataSource.getMatchesBoxSetsByBagId(bag.id)
        for(set in sets) {
            val boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
            for(box in boxes) {
                val components = dataSource.getRadioComponentsByMatchesBoxId(box.id)
                for(component in components) {
                    componentsQuantity += component.quantity
                }
            }
        }
        val displayQuantity = DisplayQuantity(bag.id, bag.name, componentsQuantity.toString())
        output.add(displayQuantity)
    }
    return output
}