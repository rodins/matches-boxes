package com.sergeyrodin.matchesboxes.component.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RadioComponentDetailsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: RadioComponentDetailsViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = RadioComponentDetailsViewModel(dataSource)
    }

    @Test
    fun componentId_detailsEqual() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id, true)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        subject.start(component.id)

        val details = subject.details.getOrAwaitValue()
        assertThat(details.bagName, `is`(bag.name))
        assertThat(details.setName, `is`(set.name))
        assertThat(details.boxName, `is`(box.name))
        assertThat(details.componentName, `is`(component.name))
        assertThat(details.componentQuantity, `is`(component.quantity.toString()))
        assertThat(details.isBuy, `is`(component.isBuy))

    }

}