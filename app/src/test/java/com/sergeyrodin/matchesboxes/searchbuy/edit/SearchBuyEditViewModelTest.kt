package com.sergeyrodin.matchesboxes.searchbuy.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.MainCoroutineRule
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchBuyEditViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: SearchBuyEditViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = SearchBuyEditViewModel(dataSource)
    }

    @Test
    fun componentId_componentNameEquals() {
        val boxId = 1
        val component = RadioComponent(1, "Component", 2, boxId)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.componentName.getOrAwaitValue()
        assertThat(name, `is`(component.name))
    }

    @Test
    fun componentId_boxNameEquals() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.boxName.getOrAwaitValue()
        assertThat(name, `is`(box.name))
    }

    @Test
    fun componentId_setNameEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.matchesBoxSetName.getOrAwaitValue()
        assertThat(name, `is`(set.name))
    }

    @Test
    fun componentId_bagNameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2,  box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        subject.start(component.id)

        val name = subject.bagName.getOrAwaitValue()
        assertThat(name, `is`(bag.name))
    }
}