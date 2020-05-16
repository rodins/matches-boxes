package com.sergeyrodin.matchesboxes.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val BAG = Bag(1, "Bag")
private val MATCHES_BOX_SET = MatchesBoxSet(1, "MatchesBoxSet", BAG.id)
private val MATCHES_BOX = MatchesBox(1, "Matches box", MATCHES_BOX_SET.id)
private val RADIO_COMPONENT = RadioComponent(1, "Radio Component", 4, MATCHES_BOX.id)

@RunWith(AndroidJUnit4::class)
@SmallTest
class RadioComponentsDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var radioComponentsDatabase: RadioComponentsDatabase

    @Before
    fun initDb() {
        radioComponentsDatabase = Room.inMemoryDatabaseBuilder(
            getApplicationContext(), RadioComponentsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() = radioComponentsDatabase.close()

    // Bag
    @Test
    fun insertAndGetBagById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getBagById(BAG.id)

        assertThat(loaded.name, `is`(BAG.name))
    }

    @Test
    fun updateAndGetBagById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)

        val bagUpdated = Bag(BAG.id, "Updated bag")
        radioComponentsDatabase.radioComponentsDatabaseDao.updateBag(bagUpdated)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getBagById(BAG.id)

        assertThat(loaded.name, `is`(bagUpdated.name))
    }

    @Test
    fun deleteBag_equalsNull() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteBag(BAG)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getBagById(BAG.id)

        assertThat(loaded, `is`(nullValue()))
    }

    // MatchesBoxSet
    @Test
    fun insertAndGetMatchesBoxSetById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded.name, `is`(MATCHES_BOX_SET.name))
        assertThat(loaded.bagId, `is`(MATCHES_BOX_SET.bagId))
    }

    @Test
    fun updateAndGetMatchesBoxSetById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        val matchesBoxesUpdated = MatchesBoxSet(MATCHES_BOX_SET.id, "Updated matches boxes", BAG.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.updateMatchesBoxSet(matchesBoxesUpdated)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded.name, `is`(matchesBoxesUpdated.name))
        assertThat(loaded.bagId, `is`(matchesBoxesUpdated.bagId))
    }

    @Test
    fun deleteMatchesBoxSet_equalsNull() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded, `is`(nullValue()))
    }

    // MatchesBox
    @Test
    fun insertAndGetMatchesBoxById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded.name, `is`(MATCHES_BOX.name))
        assertThat(loaded.matchesBoxSetId, `is`(MATCHES_BOX.matchesBoxSetId))
    }

    @Test
    fun updateAndGetMatchesBoxById()  {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        val matchesBoxUpdated = MatchesBox(MATCHES_BOX.id, "Updated matches box", MATCHES_BOX_SET.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.updateMatchesBox(matchesBoxUpdated)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded.name, `is`(matchesBoxUpdated.name))
        assertThat(loaded.matchesBoxSetId, `is`(matchesBoxUpdated.matchesBoxSetId))
    }

    @Test
    fun deleteMatchesBox_equalsNull() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteMatchesBox(MATCHES_BOX)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded, `is`(nullValue()))
    }

    // Component
    @Test
    fun insertAndGetRadioComponenById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded.name, `is`(RADIO_COMPONENT.name))
        assertThat(loaded.matchesBoxId, `is`(RADIO_COMPONENT.matchesBoxId))
    }

    @Test
    fun updateAndGetRadioComponentById() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)
        val radioComponentUpdated = RadioComponent(RADIO_COMPONENT.id,
            "Updated radio component",
            2,
            MATCHES_BOX.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.updateRadioComponent(radioComponentUpdated)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded.name, `is`(radioComponentUpdated.name))
        assertThat(loaded.matchesBoxId, `is`(radioComponentUpdated.matchesBoxId))
    }

    @Test
    fun deleteRadioComponent_equalsNull() {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteRadioComponent(RADIO_COMPONENT)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentById(
            RADIO_COMPONENT.id)

        assertThat(loaded, `is`(nullValue()))
    }
}