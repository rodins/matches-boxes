package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchesBoxSetsListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: MatchesBoxSetsListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = MatchesBoxSetsListViewModel(dataSource)
    }

    @Test
    fun bagAndNoMatchesBoxSets_sizeZero() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)

        val list = subject.matchesBoxSets.getOrAwaitValue()

        assertThat(list.size, `is`(0))
    }

    @Test
    fun noMatchesBoxSetsTextVisible_equalsTrue() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        subject.start(bag.id)

        val visible = subject.isNoMatchesBoxSetsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(true))
    }

    @Test
    fun bagAndFewMatchesBoxSets_sizeFew() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bag.id),
            MatchesBoxSet(2, "MBS2", bag.id),
            MatchesBoxSet(3, "MBS3", bag.id)
        )
        subject.start(bag.id)

        val list = subject.matchesBoxSets.getOrAwaitValue()

        assertThat(list.size, `is`(3))
    }

    @Test
    fun fewSets_noSetsText_equalsFalse() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bag.id),
            MatchesBoxSet(2, "MBS2", bag.id),
            MatchesBoxSet(3, "MBS3", bag.id)
        )
        subject.start(bag.id)

        val visible = subject.isNoMatchesBoxSetsTextVisible.getOrAwaitValue()

        assertThat(visible, `is`(false))
    }

    @Test
    fun addItem_eventNotNull() {
        subject.addItem()

        val event = subject.addItemEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun selectItem_evenNumberSelected() {
        subject.selectItem(2)

        val id = subject.selectItemEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(id, `is`(2))
    }

    @Test
    fun bagName_titleEquals(){
        val bag = Bag(1, "Bag Title")
        dataSource.addBags(bag)
        subject.start(bag.id)

        val title = subject.bagTitle.getOrAwaitValue()

        assertThat(title, `is`(bag.name))
    }
}