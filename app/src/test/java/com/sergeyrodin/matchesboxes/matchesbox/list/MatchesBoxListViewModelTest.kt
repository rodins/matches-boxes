package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchesBoxListViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: MatchesBoxListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = MatchesBoxListViewModel(dataSource)
    }

    @Test
    fun fewItems_sizeEquals() {
        val setId = 1
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", setId),
            MatchesBox(2, "Box2", setId),
            MatchesBox(3, "Box3", setId),
            MatchesBox(4, "Box4", 2)
        )
        subject.start(setId)

        val items = subject.matchesBoxes.getOrAwaitValue()
        assertThat(items.size, `is`(3))
    }

    @Test
    fun noItems_sizeZero() {
        val setId = 1
        dataSource.addMatchesBoxes()
        subject.start(setId)

        val items = subject.matchesBoxes.getOrAwaitValue()

        assertThat(items.size, `is`(0))
    }

    @Test
    fun noItems_noItemsTextVisibleIsTrue() {
        val setId = 1
        dataSource.addMatchesBoxes()
        subject.start(setId)

        val visible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun fewItems_noItemsTextVisibleIsFalse() {
        val setId = 1
        dataSource.addMatchesBoxes(
            MatchesBox(1, "Box1", setId),
            MatchesBox(2, "Box2", setId),
            MatchesBox(3, "Box3", setId)
        )
        subject.start(setId)

        val visible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addItem_eventNotNull() {
        val setId = 1
        dataSource.addMatchesBoxes()
        subject.start(setId)

        subject.addMatchesBox()

        val event = subject.addMatchesBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectItem_idEquals() {
        val setId = 1
        val matchesBoxId = 2
        dataSource.addMatchesBoxes()
        subject.start(setId)

        subject.selectMatchesBox(matchesBoxId)

        val selectedId = subject.selectMatchesBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(selectedId, `is`(matchesBoxId))
    }
}