package com.sergeyrodin.matchesboxes.util

import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
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

    @Test
    fun getMatchesBoxSetQuantityList() = runBlocking {
        val bagId = 1
        val set1 = MatchesBoxSet(1, "Set1", bagId)
        val set2 = MatchesBoxSet(2, "Set2", bagId)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component1, component2, component3, component4,
            component5, component6, component7, component8)

        val sets = dataSource.getMatchesBoxSetsByBagId(bagId)
        val setsQuantities = getMatchesBoxSetQuantityList(dataSource, sets)

        assertThat(setsQuantities[0].componentsQuantity, `is`("10"))
        assertThat(setsQuantities[1].componentsQuantity, `is`("26"))
    }

}