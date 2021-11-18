package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchesBoxManipulatorViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: MatchesBoxManipulatorViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = MatchesBoxManipulatorViewModel(dataSource)
    }

    @Test
    fun newItem_nameEmpty() {
        val setId = 1
        val boxId = ADD_NEW_ITEM_ID
        dataSource.addMatchesBoxes()
        subject.start(setId, boxId)

        val name = subject.name.getOrAwaitValue()
        assertThat(name, `is`(""))
    }

    @Test
    fun boxIdArg_nameEquals() {
        val setId = 1
        val box = MatchesBox(2, "Box", setId)
        dataSource.addMatchesBoxes(box)
        subject.start(setId, box.id)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(box.name))
    }

    @Test
    fun addItem_nameEquals() = runBlocking {
        val setId = 1
        dataSource.addMatchesBoxes()
        subject.start(setId, ADD_NEW_ITEM_ID)

        subject.setNewName("New box")

        subject.saveMatchesBox()

        val item = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)[0]
        assertThat(item.name, `is`("New box"))

        val event = subject.addEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun addTwoItems_namesEqual() = runBlocking {
        val setId = 1
        dataSource.addMatchesBoxes()
        subject.start(setId, ADD_NEW_ITEM_ID)

        subject.setNewName("Box")
        subject.saveMatchesBox()

        val item = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)[0]
        assertThat(item.name, `is`("Box"))

        subject.setNewName("Box2")
        subject.saveMatchesBox()

        val item2 = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)[1]
        assertThat(item2.name, `is`("Box2"))
    }

    @Test
    fun updateItem_nameEquals() = runBlocking{
        val setId = 2
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        subject.start(setId, box.id)

        subject.setNewName("Box updated")
        subject.saveMatchesBox()

        val item = dataSource.getMatchesBoxById(box.id)
        assertThat(item?.name, `is`("Box updated"))

        val event = subject.updateEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun deleteItem_itemIsNull() = runBlocking{
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        subject.start(set.id, box.id)

        subject.deleteMatchesBox()

        val item = dataSource.getMatchesBoxById(box.id)
        assertThat(item, `is`(nullValue()))

        val event = subject.deleteEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun updateItem_boxNameEquals() {
        val setId = 2
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        subject.start(setId, box.id)

        subject.setNewName("Box updated")
        subject.saveMatchesBox()

        val title = subject.updateEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(title, `is`(box.name))
    }

    @Test
    fun addItem_setTitleEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes()
        subject.start(set.id, ADD_NEW_ITEM_ID)

        subject.setNewName("New box")

        subject.saveMatchesBox()

        val title = subject.addEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(title, `is`(set.name))
    }

    @Test
    fun deleteItem_setTitleEquals() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        subject.start(set.id, box.id)

        subject.deleteMatchesBox()

        val loaded = subject.deleteEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(loaded?.name, `is`(set.name))
    }
}