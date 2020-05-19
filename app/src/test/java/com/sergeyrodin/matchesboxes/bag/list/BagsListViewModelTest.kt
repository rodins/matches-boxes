package com.sergeyrodin.matchesboxes.bag.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BagsListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: BagsListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = BagsListViewModel(dataSource)
    }

    @Test
    fun addItems_sizeEquals() {
        dataSource.addBags(
            Bag(1, "Bag1"),
            Bag(2, "Bag2"),
            Bag(3, "Bag3"),
            Bag(4, "Bag4")
        )

        val bags = subject.bagsList.getOrAwaitValue()

        assertThat(bags.size, `is`(4))
    }

    @Test
    fun noItems_sizeZero() {
        dataSource.addBags()
        val bags = subject.bagsList.getOrAwaitValue()

        assertThat(bags.size, `is`(0))
    }

    @Test
    fun noItems_noItemsTextVisible_isTrue() {
        dataSource.addBags()
        val noItemsTextVisible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(noItemsTextVisible, `is`(true))
    }

    @Test
    fun addItems_noItemsTextVisible_isFalse() {
        dataSource.addBags(
            Bag(1, "Bag1"),
            Bag(2, "Bag2"),
            Bag(3, "Bag3"),
            Bag(4, "Bag4")
        )

        val noItemsTextVisible = subject.isNoItemsTextVisible.getOrAwaitValue()

        assertThat(noItemsTextVisible, `is`(false))
    }

    @Test
    fun addItem_eventNotNull() {
        dataSource.addBags()

        subject.addItem()

        val event = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectItem_eventNumberMatches() {
        dataSource.addBags()

        subject.selectItem(1)

        val value = subject.selectItemEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(value, `is`(1))
    }
}