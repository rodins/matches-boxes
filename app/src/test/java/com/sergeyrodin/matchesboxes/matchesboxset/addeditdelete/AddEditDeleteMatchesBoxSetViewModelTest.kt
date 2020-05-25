package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditDeleteMatchesBoxSetViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: AddEditDeleteMatchesBoxSetViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = AddEditDeleteMatchesBoxSetViewModel(dataSource)
    }

    @Test
    fun addNewSet_nameEquals() = runBlocking{
        val bagId = 1
        subject.start(bagId, -1)

        subject.saveMatchesBoxSet("MBS1")

        val set = dataSource.getMatchesBoxSetsByBagId(bagId)[0]
        val added = subject.addedEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(set.name, `is`("MBS1"))
        assertThat(added, `is`(not(nullValue())))
    }

    @Test
    fun updateSet_nameEquals() = runBlocking{
        val bagId = 1
        val set = MatchesBoxSet(2, "MBS2", bagId)
        dataSource.addMatchesBoxSets(set)
        subject.start(bagId, set.id)

        subject.saveMatchesBoxSet("MBS2 updated")

        val setUpdated = dataSource.getMatchesBoxSetById(set.id)
        val updated = subject.updatedEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(setUpdated?.name, `is`("MBS2 updated"))
        assertThat(updated, `is`(not(nullValue())))
    }

    @Test
    fun deleteSet_sizeZero() = runBlocking{
        val bagId = 1
        val set = MatchesBoxSet(2, "MBS2", bagId)
        dataSource.addMatchesBoxSets(set)
        subject.start(bagId, set.id)

        subject.deleteMatchesBoxSet()

        val sets = dataSource.getMatchesBoxSetsByBagId(bagId)
        val deleted = subject.deletedEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(sets.size, `is`(0))
    }

    @Test
    fun newItem_nameEmpty() {
        val bagId = 1
        val setId = -1
        subject.start(bagId, setId)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(""))
    }

    @Test
    fun notNewItem_nameEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "MBS1", bagId)
        dataSource.addMatchesBoxSets(set)
        subject.start(bagId, set.id)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(set.name))
    }

}