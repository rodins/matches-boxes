package com.sergeyrodin.matchesboxes.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
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

        assertThat(loaded?.name, `is`(BAG.name))
    }

    @Test
    fun updateAndGetBagById() = runBlockingTest{
        subject.insertBag(BAG)

        val bagUpdated = Bag(BAG.id, "Updated bag")
        subject.updateBag(bagUpdated)

        val loaded = subject.getBagById(BAG.id)

        assertThat(loaded?.name, `is`(bagUpdated.name))
    }

    @Test
    fun deleteBag_equalsNull() = runBlockingTest {
        subject.insertBag(BAG)
        subject.deleteBag(BAG)
        val loaded = subject.getBagById(BAG.id)

        assertThat(loaded, `is`(CoreMatchers.nullValue()))
    }

    // MatchesBoxSet
    @Test
    fun insertAndGetMatchesBoxSetById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = subject.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded?.name, `is`(MATCHES_BOX_SET.name))
        assertThat(loaded?.bagId, `is`(MATCHES_BOX_SET.bagId))
    }

    @Test
    fun updateAndGetMatchesBoxSetById() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        val matchesBoxesUpdated = MatchesBoxSet(MATCHES_BOX_SET.id, "Updated matches boxes", BAG.id)
        subject.updateMatchesBoxSet(matchesBoxesUpdated)

        val loaded = subject.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded?.name, `is`(matchesBoxesUpdated.name))
        assertThat(loaded?.bagId, `is`(matchesBoxesUpdated.bagId))
    }

    @Test
    fun deleteMatchesBoxSet_equalsNull() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.deleteMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = subject.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded, `is`(CoreMatchers.nullValue()))
    }

    // MatchesBox
    @Test
    fun insertAndGetMatchesBoxById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)

        val loaded = subject.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded?.name, `is`(MATCHES_BOX.name))
        assertThat(loaded?.matchesBoxSetId, `is`(MATCHES_BOX.matchesBoxSetId))
    }

    @Test
    fun updateAndGetMatchesBoxById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        val matchesBoxUpdated = MatchesBox(MATCHES_BOX.id, "Updated matches box", MATCHES_BOX_SET.id)
        subject.updateMatchesBox(matchesBoxUpdated)

        val loaded = subject.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded?.name, `is`(matchesBoxUpdated.name))
        assertThat(loaded?.matchesBoxSetId, `is`(matchesBoxUpdated.matchesBoxSetId))
    }

    @Test
    fun deleteMatchesBox_equalsNull() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.deleteMatchesBox(MATCHES_BOX)

        val loaded = subject.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded, `is`(CoreMatchers.nullValue()))
    }

    // Component
    @Test
    fun insertAndGetRadioComponenById() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded?.name, `is`(RADIO_COMPONENT.name))
        assertThat(loaded?.quantity, `is`(RADIO_COMPONENT.quantity))
        assertThat(loaded?.matchesBoxId, `is`(RADIO_COMPONENT.matchesBoxId))
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

        assertThat(loaded?.name, `is`(radioComponentUpdated.name))
        assertThat(loaded?.quantity, `is`(radioComponentUpdated.quantity))
        assertThat(loaded?.matchesBoxId, `is`(radioComponentUpdated.matchesBoxId))
    }

    @Test
    fun deleteRadioComponent_equalsNull() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)
        subject.deleteRadioComponent(RADIO_COMPONENT)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded, `is`(CoreMatchers.nullValue()))
    }

    @Test
    fun deleteBag_radioComponentIsNull() = runBlockingTest{
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        subject.insertMatchesBox(MATCHES_BOX)
        subject.insertRadioComponent(RADIO_COMPONENT)
        subject.deleteBag(BAG)

        val loaded = subject.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded, `is`(CoreMatchers.nullValue()))
    }

    // LiveData tests

    @Test
    fun addFourBags_sizeEquals() = runBlockingTest {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val bag3 = Bag(3, "Bag3")
        val bag4 = Bag(4, "Bag4")
        subject.insertBag(bag1)
        subject.insertBag(bag2)
        subject.insertBag(bag3)
        subject.insertBag(bag4)

        val loaded = subject.getBags().getOrAwaitValue()

        assertThat(loaded.size, `is`(4))
    }

    @Test
    fun addFiveMatchesBoxSets_sizeEquals() = runBlockingTest {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        subject.insertBag(bag1)
        subject.insertBag(bag2)
        val matchesBoxSet1 = MatchesBoxSet(1, "MBS1", bag1.id)
        val matchesBoxSet2 = MatchesBoxSet(2, "MBS2", bag1.id)
        val matchesBoxSet3 = MatchesBoxSet(3, "MBS3", bag2.id)
        val matchesBoxSet4 = MatchesBoxSet(4, "MBS4", bag2.id)
        val matchesBoxSet5 = MatchesBoxSet(5, "MBS5", bag2.id)
        subject.insertMatchesBoxSet(matchesBoxSet1)
        subject.insertMatchesBoxSet(matchesBoxSet2)
        subject.insertMatchesBoxSet(matchesBoxSet3)
        subject.insertMatchesBoxSet(matchesBoxSet4)
        subject.insertMatchesBoxSet(matchesBoxSet5)

        val loaded1 = subject.getMatchesBoxSetsByBagId(bag1.id)
        val loaded2 = subject.getMatchesBoxSetsByBagId(bag2.id)

        assertThat(loaded1.size, `is`(2))
        assertThat(loaded2.size, `is`(3))
    }

    @Test
    fun bagAndNoMatchesBoxSets_sizeZero() = runBlockingTest{
        subject.insertBag(BAG)

        val list = subject.getMatchesBoxSetsByBagId(BAG.id)

        assertThat(list.size, `is`(0))
    }

    @Test
    fun addFiveMatchesBoxes_sizeEquals() = runBlockingTest {
        subject.insertBag(BAG)
        val matchesBoxSet1 = MatchesBoxSet(1, "MBS1", BAG.id)
        val matchesBoxSet2 = MatchesBoxSet(2, "MBS2", BAG.id)
        subject.insertMatchesBoxSet(matchesBoxSet1)
        subject.insertMatchesBoxSet(matchesBoxSet2)
        subject.insertMatchesBox(MatchesBox(1, "MB1", matchesBoxSet1.id))
        subject.insertMatchesBox(MatchesBox(2, "MB2", matchesBoxSet1.id))
        subject.insertMatchesBox(MatchesBox(3, "MB3", matchesBoxSet2.id))
        subject.insertMatchesBox(MatchesBox(4, "MB4", matchesBoxSet2.id))
        subject.insertMatchesBox(MatchesBox(5, "MB5", matchesBoxSet2.id))

        val loaded1 = subject.getMatchesBoxesByMatchesBoxSetId(matchesBoxSet1.id)
        val loaded2 = subject.getMatchesBoxesByMatchesBoxSetId(matchesBoxSet2.id)

        assertThat(loaded1.size, `is`(2))
        assertThat(loaded2.size, `is`(3))
    }

    @Test
    fun addFewRadioComponents_sizeEquals() = runBlockingTest {
        subject.insertBag(BAG)
        subject.insertMatchesBoxSet(MATCHES_BOX_SET)
        val matchesBox1 = MatchesBox(1, "MB1", MATCHES_BOX_SET.id)
        val matchesBox2 = MatchesBox(2, "MB2", MATCHES_BOX_SET.id)
        subject.insertMatchesBox(matchesBox1)
        subject.insertMatchesBox(matchesBox2)
        subject.insertRadioComponent(RadioComponent(1, "RC1", 4, matchesBox1.id))
        subject.insertRadioComponent(RadioComponent(2, "RC2", 10, matchesBox1.id))
        subject.insertRadioComponent(RadioComponent(3, "RC3", 5, matchesBox2.id))
        subject.insertRadioComponent(RadioComponent(4, "RC4", 5, matchesBox2.id))
        subject.insertRadioComponent(RadioComponent(5, "RC5", 5, matchesBox2.id))

        val loaded1 = subject.getRadioComponentsByMatchesBoxId(matchesBox1.id).getOrAwaitValue()
        val loaded2 = subject.getRadioComponentsByMatchesBoxId(matchesBox2.id).getOrAwaitValue()

        assertThat(loaded1.size, `is`(2))
        assertThat(loaded2.size, `is`(3))
    }
}