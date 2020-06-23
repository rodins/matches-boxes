package com.sergeyrodin.matchesboxes.util

import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

data class MatchesBoxQuantity(
    val id: Int,
    val name: String,
    val setId: Int,
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
        val boxQuantity = MatchesBoxQuantity(box.id, box.name, box.matchesBoxSetId, componentsQuantity.toString())
        output.add(boxQuantity)
    }
    return output
}