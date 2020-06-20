package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.MainCoroutineRule
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditDeleteBagViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: AddEditDeleteBagViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = AddEditDeleteBagViewModel(dataSource)
    }

    @Test
    fun addNewBag_nameEquals() {
        subject.start(-1)
        subject.name.value = "New bag"
        subject.saveBag()

        val bag = dataSource.getBags().getOrAwaitValue()[0]
        assertThat(bag.name, `is`("New bag"))
    }

    @Test
    fun editBag_nameEquals() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)
        subject.name.value = "Updated bag"
        subject.saveBag()

        val bagUpdated = dataSource.getBags().getOrAwaitValue()[0]
        assertThat(bagUpdated.name, `is`("Updated bag"))
    }

    @Test
    fun deleteBag_sizeZero() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)

        subject.deleteBag()

        val bagsList = dataSource.getBags().getOrAwaitValue()
        assertThat(bagsList.size, `is`(0))
    }

    @Test
    fun idAdded_nameEquals() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(bag.name))
    }

    @Test
    fun nullAdded_nameIsEmpty() {
        dataSource.addBags()
        subject.start(-1)

        val name = subject.name.getOrAwaitValue()

        assertThat(name, `is`(""))
    }

    @Test
    fun bagAdded_eventTrue() {
        dataSource.addBags()
        subject.start(-1)
        subject.name.value = "New bag"
        subject.saveBag()

        val added = subject.eventAdded.getOrAwaitValue().getContentIfNotHandled()
        assertThat(added, `is`(not(nullValue())))
    }

    @Test
    fun bagEdited_eventTrue() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)
        subject.name.value = "Updated bag"
        subject.saveBag()

        val edited = subject.eventEdited.getOrAwaitValue().getContentIfNotHandled()
        assertThat(edited, `is`(not(nullValue())))
    }

    @Test
    fun bagDeleted_eventTrue() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)

        subject.deleteBag()

        val deleted = subject.eventDeleted.getOrAwaitValue().getContentIfNotHandled()
        assertThat(deleted, `is`(not(nullValue())))
    }
}