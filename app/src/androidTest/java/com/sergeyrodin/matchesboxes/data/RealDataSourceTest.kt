package com.sergeyrodin.matchesboxes.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val BAG = Bag(1, "Bag")
private val MATCHES_BOX_SET = MatchesBoxSet(1, "MatchesBoxSet", BAG.id)
private val MATCHES_BOX = MatchesBox(1, "Matches box", MATCHES_BOX_SET.id)
private val RADIO_COMPONENT = RadioComponent(1, "Radio Component", 4, MATCHES_BOX.id)

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RealDataSourceTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var radioComponentsDatabase: RadioComponentsDatabase

    private lateinit var subject: RadioComponentsDataSource

    @Before
    fun initDb() {
        radioComponentsDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RadioComponentsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        subject = RealDataSource(radioComponentsDatabase.radioComponentsDatabaseDao)
    }

    @After
    fun closeDb() = radioComponentsDatabase.close()

    // Bag
    @Test
    fun insertAndGetBagById() = runBlockingTest{
        subject.insertBag(BAG)

        val loaded = subject.getBagById(BAG.id)

        assertThat(loaded.name, CoreMatchers.`is`(BAG.name))
    }

    @Test
    fun updateAndGetBagById() = runBlockingTest{
        subject.insertBag(BAG)

        val bagUpdated = Bag(BAG.id, "Updated bag")
        subject.updateBag(bagUpdated)

        val loaded = subject.getBagById(BAG.id)

        assertThat(loaded.name, CoreMatchers.`is`(bagUpdated.name))
    }

    @Test
    fun deleteBag_equalsNull() = runBlockingTest {
        subject.insertBag(BAG)
        subject.deleteBag(BAG)
        val loaded = subject.getBagById(BAG.id)

        assertThat(loaded, CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    // MatchesBoxSet
    @Test
    fun insertAndGetMatchesBoxSetById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = subject.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded.name, CoreMatchers.`is`(MATCHES_BOX_SET.name))
        assertThat(loaded.bagId, CoreMatchers.`is`(MATCHES_BOX_SET.bagId))
    }

    @Test
    fun updateAndGetMatchesBoxSetById() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        val matchesBoxesUpdated = MatchesBoxSet(MATCHES_BOX_SET.id, "Updated matches boxes", BAG.id)
        subject.updateMatchesBoxSet(matchesBoxesUpdated)

        val loaded = subject.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded.name, CoreMatchers.`is`(matchesBoxesUpdated.name))
        assertThat(loaded.bagId, CoreMatchers.`is`(matchesBoxesUpdated.bagId))
    }

    @Test
    fun deleteMatchesBoxSet_equalsNull() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.deleteMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = subject.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded, CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    // MatchesBox
    @Test
    fun insertAndGetMatchesBoxById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)

        val loaded = subject.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded.name, CoreMatchers.`is`(MATCHES_BOX.name))
        assertThat(loaded.matchesBoxSetId, CoreMatchers.`is`(MATCHES_BOX.matchesBoxSetId))
    }

    @Test
    fun updateAndGetMatchesBoxById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        val matchesBoxUpdated = MatchesBox(MATCHES_BOX.id, "Updated matches box", MATCHES_BOX_SET.id)
        subject.updateMatchesBox(matchesBoxUpdated)

        val loaded = subject.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded.name, CoreMatchers.`is`(matchesBoxUpdated.name))
        assertThat(loaded.matchesBoxSetId, CoreMatchers.`is`(matchesBoxUpdated.matchesBoxSetId))
    }

    @Test
    fun deleteMatchesBox_equalsNull() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.deleteMatchesBox(MATCHES_BOX)

        val loaded = subject.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded, CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    // Component
    @Test
    fun insertAndGetRadioComponenById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded.name, CoreMatchers.`is`(RADIO_COMPONENT.name))
        assertThat(loaded.matchesBoxId, CoreMatchers.`is`(RADIO_COMPONENT.matchesBoxId))
    }

    @Test
    fun updateAndGetRadioComponentById() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)
        val radioComponentUpdated = RadioComponent(RADIO_COMPONENT.id,
            "Updated radio component",
            2,
            MATCHES_BOX.id)
        subject.updateRadioComponent(radioComponentUpdated)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded.name, CoreMatchers.`is`(radioComponentUpdated.name))
        assertThat(loaded.matchesBoxId, CoreMatchers.`is`(radioComponentUpdated.matchesBoxId))
    }

    @Test
    fun deleteRadioComponent_equalsNull() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)
        subject.deleteRadioComponent(RADIO_COMPONENT)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded, CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun deleteBag_radioComponentIsNull() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)
        subject.deleteBag(BAG)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded, CoreMatchers.`is`(CoreMatchers.nullValue()))
    }
}