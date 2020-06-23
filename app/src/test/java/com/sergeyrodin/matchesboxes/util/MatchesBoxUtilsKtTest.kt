package com.sergeyrodin.matchesboxes.util

import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponent
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class MatchesBoxUtilsKtTest {

    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
    }

    @Test
    fun getMatchesBoxesQuantityList() = runBlocking{
        val setId = 1
        val box1 = MatchesBox(1, "Box1", setId)
        val box2 = MatchesBox(2, "Box2", setId)
        val component1 = RadioComponent(1, "Component1", 3, box1.id)
        val component2 = RadioComponent(1, "Component2", 4, box1.id)
        val component3 = RadioComponent(1, "Component3", 5, box2.id)
        val component4 = RadioComponent(1, "Component4", 6, box2.id)
        dataSource.addMatchesBoxes(box1, box2)
        dataSource.addRadioComponents(component1, component2, component3, component4)

        val matchesBoxes = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        val matchesBoxesQuantities = getMatchesBoxesQuantityList(dataSource, matchesBoxes)

        assertThat(matchesBoxesQuantities[0].componentsQuantity, `is`("7"))
        assertThat(matchesBoxesQuantities[1].componentsQuantity, `is`("11"))
    }
}