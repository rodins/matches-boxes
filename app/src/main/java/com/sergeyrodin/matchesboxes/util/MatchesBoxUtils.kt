package com.sergeyrodin.matchesboxes.util

import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

data class MatchesBoxQuantity(
    val id: Int,
    val name: String,
    val componentsQuantity: String
)

data class MatchesBoxSetQuantity(
    val id: Int,
    val name: String,
    val componentsQuantity: String
)

suspend fun getMatchesBoxesQuantityList(dataSource: RadioComponentsDataSource, matchesBoxes: List<MatchesBox>): List<MatchesBoxQuantity> {
    val output = mutableListOf<MatchesBoxQuantity>()
    for (box in matchesBoxes) {
        val components = dataSource.getRadioComponentsByMatchesBoxId(box.id)
        var componentsQuantity = 0
        for(component in components) {
            componentsQuantity += component.quantity
        }
        val boxQuantity = MatchesBoxQuantity(box.id, box.name, componentsQuantity.toString())
        output.add(boxQuantity)
    }
    return output
}

suspend fun getMatchesBoxSetQuantityList(dataSource: RadioComponentsDataSource, sets: List<MatchesBoxSet>): List<MatchesBoxSetQuantity> {
    val output = mutableListOf<MatchesBoxSetQuantity>()
    for (set in sets) {
        val boxes = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
        var componentsQuantity = 0
        for(box in boxes) {
            val components = dataSource.getRadioComponentsByMatchesBoxId(box.id)
            for(component in components) {
                componentsQuantity += component.quantity
            }
        }
        val setQuantity = MatchesBoxSetQuantity(set.id, set.name, componentsQuantity.toString())
        output.add(setQuantity)
    }
    return output
}