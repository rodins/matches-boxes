package com.sergeyrodin.matchesboxes.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sergeyrodin.matchesboxes.getOrAwaitValue
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

@ExperimentalCoroutinesApi
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
    fun insertAndGetBagById() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getBagById(BAG.id)

        assertThat(loaded.name, `is`(BAG.name))
    }

    @Test
    fun updateAndGetBagById() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)

        val bagUpdated = Bag(BAG.id, "Updated bag")
        radioComponentsDatabase.radioComponentsDatabaseDao.updateBag(bagUpdated)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getBagById(BAG.id)

        assertThat(loaded.name, `is`(bagUpdated.name))
    }

    @Test
    fun deleteBag_equalsNull() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteBag(BAG)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getBagById(BAG.id)

        assertThat(loaded, `is`(nullValue()))
    }

    // MatchesBoxSet
    @Test
    fun insertAndGetMatchesBoxSetById() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded.name, `is`(MATCHES_BOX_SET.name))
        assertThat(loaded.bagId, `is`(MATCHES_BOX_SET.bagId))
    }

    @Test
    fun updateAndGetMatchesBoxSetById() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        val matchesBoxesUpdated = MatchesBoxSet(MATCHES_BOX_SET.id, "Updated matches boxes", BAG.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.updateMatchesBoxSet(matchesBoxesUpdated)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded.name, `is`(matchesBoxesUpdated.name))
        assertThat(loaded.bagId, `is`(matchesBoxesUpdated.bagId))
    }

    @Test
    fun deleteMatchesBoxSet_equalsNull() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteMatchesBoxSet(MATCHES_BOX_SET)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxSetById(MATCHES_BOX_SET.id)

        assertThat(loaded, `is`(nullValue()))
    }

    // MatchesBox
    @Test
    fun insertAndGetMatchesBoxById() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded.name, `is`(MATCHES_BOX.name))
        assertThat(loaded.matchesBoxSetId, `is`(MATCHES_BOX.matchesBoxSetId))
    }

    @Test
    fun updateAndGetMatchesBoxById() = runBlockingTest {
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
    fun deleteMatchesBox_equalsNull() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteMatchesBox(MATCHES_BOX)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getMatchesBoxById(MATCHES_BOX.id)

        assertThat(loaded, `is`(nullValue()))
    }

    // Component
    @Test
    fun insertAndGetRadioComponenById() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentById(RADIO_COMPONENT.id)

        assertThat(loaded.name, `is`(RADIO_COMPONENT.name))
        assertThat(loaded.matchesBoxId, `is`(RADIO_COMPONENT.matchesBoxId))
    }

    @Test
    fun updateAndGetRadioComponentById() = runBlockingTest{
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
    fun deleteRadioComponent_equalsNull() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteRadioComponent(RADIO_COMPONENT)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentById(
            RADIO_COMPONENT.id)

        assertThat(loaded, `is`(nullValue()))
    }

    @Test
    fun deleteBag_radioComponentIsNull() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)
        radioComponentsDatabase.radioComponentsDatabaseDao.deleteBag(BAG)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentById(
            RADIO_COMPONENT.id)

        assertThat(loaded, `is`(nullValue()))
    }

    @Test
    fun getDisplayQuantityListBySetId() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        val set1 = MatchesBoxSet(1, "Set1", BAG.id)
        val set2 = MatchesBoxSet(2, "Set2", BAG.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set2)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box4)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component1", 2, box1.id)
        val component3 = RadioComponent(3, "Component1", 3, box2.id)
        val component4 = RadioComponent(4, "Component1", 4, box2.id)
        val component5 = RadioComponent(5, "Component1", 5, box3.id)
        val component6 = RadioComponent(6, "Component1", 6, box3.id)
        val component7 = RadioComponent(7, "Component1", 7, box4.id)
        val component8 = RadioComponent(8, "Component1", 8, box4.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component4)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component5)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component6)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component7)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component8)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getDisplayQuantityListBySetId(set1.id)
        assertThat(list[0].name, `is`(box1.name))
        assertThat(list[0].componentsQuantity, `is`("3"))
        assertThat(list[1].name, `is`(box2.name))
        assertThat(list[1].componentsQuantity, `is`("7"))
    }

    @Test
    fun oneBox_sizeEquals() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getDisplayQuantityListBySetId(MATCHES_BOX_SET.id)
        assertThat(list.size, `is`(1))
    }

    @Test
    fun getDisplayQuantityListByBagId() = runBlockingTest {
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        val set1 = MatchesBoxSet(1, "Set1", BAG.id)
        val set2 = MatchesBoxSet(2, "Set2", BAG.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set2)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box4)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component1", 2, box1.id)
        val component3 = RadioComponent(3, "Component1", 3, box2.id)
        val component4 = RadioComponent(4, "Component1", 4, box2.id)
        val component5 = RadioComponent(5, "Component1", 5, box3.id)
        val component6 = RadioComponent(6, "Component1", 6, box3.id)
        val component7 = RadioComponent(7, "Component1", 7, box4.id)
        val component8 = RadioComponent(8, "Component1", 8, box4.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component4)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component5)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component6)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component7)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component8)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getDisplayQuantityListByBagId(BAG.id)
        assertThat(list[0].name, `is`(set1.name))
        assertThat(list[0].componentsQuantity, `is`("10"))
        assertThat(list[1].name, `is`(set2.name))
        assertThat(list[1].componentsQuantity, `is`("26"))
    }

    @Test
    fun oneSet_sizeEquals() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getDisplayQuantityListByBagId(BAG.id)
        assertThat(list.size, `is`(1))
    }

    @Test
    fun getBagsDisplayQuantityList() = runBlockingTest{
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(bag1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(bag2)
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set4)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box4)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box5)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box6)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box7)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box8)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component1", 2, box1.id)
        val component3 = RadioComponent(3, "Component1", 3, box2.id)
        val component4 = RadioComponent(4, "Component1", 4, box2.id)
        val component5 = RadioComponent(5, "Component1", 5, box3.id)
        val component6 = RadioComponent(6, "Component1", 6, box3.id)
        val component7 = RadioComponent(7, "Component1", 7, box4.id)
        val component8 = RadioComponent(8, "Component1", 8, box4.id)
        val component9 = RadioComponent(9, "Component1", 9, box5.id)
        val component10 = RadioComponent(10, "Component1", 10, box5.id)
        val component11 = RadioComponent(11, "Component1", 11, box6.id)
        val component12 = RadioComponent(12, "Component1", 12, box6.id)
        val component13 = RadioComponent(13, "Component1", 13, box7.id)
        val component14 = RadioComponent(14, "Component1", 14, box7.id)
        val component15 = RadioComponent(15, "Component1", 15, box8.id)
        val component16 = RadioComponent(16, "Component1", 16, box8.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component4)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component5)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component6)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component7)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component8)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component9)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component10)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component11)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component12)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component13)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component14)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component15)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component16)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getBagsDisplayQuantityList().getOrAwaitValue()
        assertThat(list[0].name, `is`(bag1.name))
        assertThat(list[0].componentsQuantity, `is`("36"))
        assertThat(list[1].name, `is`(bag2.name))
        assertThat(list[1].componentsQuantity, `is`("100"))
    }

    @Test
    fun oneBagInput_getBagQuantityList() = runBlockingTest{
        val bag1 = Bag(1, "Bag1")
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(bag1)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getBagsDisplayQuantityList().getOrAwaitValue()
        assertThat(list.size, `is`(1))
    }

    @Test
    fun boxesSort_sortById() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)

        val box1 = MatchesBox(1, "CBox", MATCHES_BOX_SET.id)
        val box2 = MatchesBox(2, "BBox", MATCHES_BOX_SET.id)
        val box3 = MatchesBox(3, "ABox", MATCHES_BOX_SET.id)

        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(box3)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getDisplayQuantityListBySetId(MATCHES_BOX_SET.id)
        assertThat(list[0].name, `is`(box1.name))
        assertThat(list[1].name, `is`(box2.name))
        assertThat(list[2].name, `is`(box3.name))
    }

    @Test
    fun setsSort_sortById() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)

        val set1 = MatchesBoxSet(1, "CSet", BAG.id)
        val set2 = MatchesBoxSet(2, "BSet", BAG.id)
        val set3 = MatchesBoxSet(3, "ASet", BAG.id)

        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(set3)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getDisplayQuantityListByBagId(BAG.id)

        assertThat(list[0].name, `is`(set1.name))
        assertThat(list[1].name, `is`(set2.name))
        assertThat(list[2].name, `is`(set3.name))
    }

    @Test
    fun bagsSort_sortById() = runBlockingTest{
        val bag1 = Bag(1, "CBag")
        val bag2 = Bag(2, "BBag")
        val bag3 = Bag(3, "ABag")

        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(bag1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(bag2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(bag3)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getBagsDisplayQuantityList().getOrAwaitValue()
        assertThat(list[0].name, `is`(bag1.name))
        assertThat(list[1].name, `is`(bag2.name))
        assertThat(list[2].name, `is`(bag3.name))
    }

    // RadioComponentDetails

    @Test
    fun getRadioComponentDetailsById() = runBlockingTest {
        val component = RadioComponent(1, "Component", 3, MATCHES_BOX.id, true)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component)

        val details = radioComponentsDatabase.radioComponentsDatabaseDao.getRadioComponentDetailsById(component.id)

        assertThat(details.bagName, `is`(BAG.name))
        assertThat(details.setName, `is`(MATCHES_BOX_SET.name))
        assertThat(details.boxName, `is`(MATCHES_BOX.name))
        assertThat(details.componentName, `is`(component.name))
        assertThat(details.componentQuantity, `is`(component.quantity.toString()))
        assertThat(details.isBuy, `is`(component.isBuy))
    }

    // History

    @Test
    fun addHistory_componentIdEquals() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(RADIO_COMPONENT)

        val history = History(1, RADIO_COMPONENT.id, RADIO_COMPONENT.quantity)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history)

        val loaded = radioComponentsDatabase.radioComponentsDatabaseDao.getHistoryById(history.id)
        assertThat(loaded.componentId, `is`(RADIO_COMPONENT.id))
    }

    @Test
    fun getHistoryList_sizeEquals() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        val component1 = RadioComponent(1, "Component1", 4, MATCHES_BOX.id)
        val component2 = RadioComponent(2, "Component2", 4, MATCHES_BOX.id)
        val component3 = RadioComponent(3, "Component3", 4, MATCHES_BOX.id)
        val component4 = RadioComponent(4, "Component4", 4, MATCHES_BOX.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component4)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component3.id, component3.quantity)
        val history4 = History(4, component4.id, component4.quantity)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history4)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getHistoryList()
        assertThat(list.size, `is`(4))
    }

    @Test
    fun getHistoryList_descendingOrder_firstElementEqualsLastInserted() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        val component1 = RadioComponent(1, "Component1", 4, MATCHES_BOX.id)
        val component2 = RadioComponent(2, "Component2", 4, MATCHES_BOX.id)
        val component3 = RadioComponent(3, "Component3", 4, MATCHES_BOX.id)
        val component4 = RadioComponent(4, "Component4", 4, MATCHES_BOX.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component4)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component2.id, component2.quantity)
        val history3 = History(3, component3.id, component3.quantity)
        val history4 = History(4, component4.id, component4.quantity)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history4)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getHistoryList()
        assertThat(list[0], `is`(history4))
    }

    @Test
    fun getHistoryListByComponentId_sizeEquals() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        val component1 = RadioComponent(1, "Component1", 4, MATCHES_BOX.id)
        val component2 = RadioComponent(2, "Component2", 4, MATCHES_BOX.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component1.id, component1.quantity)
        val history3 = History(3, component1.id, component1.quantity)
        val history4 = History(4, component2.id, component2.quantity)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history4)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getHistoryListByComponentId(component1.id)
        assertThat(list.size, `is`(3))
    }

    @Test
    fun getHistoryListByComponentId_orderDescending_firstListElementEqualsLastInserted() = runBlockingTest{
        radioComponentsDatabase.radioComponentsDatabaseDao.insertBag(BAG)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBoxSet(MATCHES_BOX_SET)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertMatchesBox(MATCHES_BOX)
        val component1 = RadioComponent(1, "Component1", 4, MATCHES_BOX.id)
        val component2 = RadioComponent(2, "Component2", 4, MATCHES_BOX.id)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertRadioComponent(component2)
        val history1 = History(1, component1.id, component1.quantity)
        val history2 = History(2, component1.id, component1.quantity)
        val history3 = History(3, component1.id, component1.quantity)
        val history4 = History(4, component2.id, component2.quantity)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history1)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history2)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history3)
        radioComponentsDatabase.radioComponentsDatabaseDao.insertHistory(history4)

        val list = radioComponentsDatabase.radioComponentsDatabaseDao.getHistoryListByComponentId(component1.id)
        assertThat(list[0], `is`(history3))
    }
}