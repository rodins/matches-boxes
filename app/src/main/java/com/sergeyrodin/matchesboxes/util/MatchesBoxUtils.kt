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