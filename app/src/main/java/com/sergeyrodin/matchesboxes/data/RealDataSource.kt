package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import com.sergeyrodin.matchesboxes.util.wrapEspressoIdlingResource
import javax.inject.Inject

class RealDataSource @Inject constructor(
    private val radioComponentsDatabaseDao: RadioComponentsDatabaseDao) : RadioComponentsDataSource {
    override suspend fun insertBag(bag: Bag) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.insertBag(bag)
        }
    }

    override suspend fun updateBag(bag: Bag) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.updateBag(bag)
        }
    }

    override suspend fun deleteBag(bag: Bag) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.deleteBag(bag)
        }
    }

    override suspend fun getBagById(bagId: Int): Bag? {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getBagById(bagId)
        }
    }

    override suspend fun getBags(): List<Bag> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getBags()
        }
    }

    override suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.insertMatchesBoxSet(matchesBoxSet)
        }

    }

    override suspend fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.updateMatchesBoxSet(matchesBoxSet)
        }
    }

    override suspend fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.deleteMatchesBoxSet(matchesBoxSet)
        }

    }

    override suspend fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet? {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getMatchesBoxSetById(matchesBoxSetId)
        }
    }

    override suspend fun getMatchesBoxSetsByBagId(bagId: Int): List<MatchesBoxSet> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getMatchesBoxSetsByBagId(bagId)
        }
    }

    override suspend fun insertMatchesBox(matchesBox: MatchesBox) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.insertMatchesBox(matchesBox)
        }
    }

    override suspend fun updateMatchesBox(matchesBox: MatchesBox) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.updateMatchesBox(matchesBox)
        }

    }

    override suspend fun deleteMatchesBox(matchesBox: MatchesBox) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.deleteMatchesBox(matchesBox)
        }
    }

    override suspend fun getMatchesBoxById(matchesBoxId: Int): MatchesBox? {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getMatchesBoxById(matchesBoxId)
        }
    }

    override suspend fun getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId: Int): List<MatchesBox> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId)
        }
    }

    override suspend fun insertRadioComponent(radioComponent: RadioComponent): Long {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.insertRadioComponent(radioComponent)
        }
    }

    override suspend fun updateRadioComponent(radioComponent: RadioComponent) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.updateRadioComponent(radioComponent)
        }
    }

    override suspend fun deleteRadioComponent(radioComponent: RadioComponent) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.deleteRadioComponent(radioComponent)
        }
    }

    override suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent? {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getRadioComponentById(radioComponentId)
        }
    }

    override suspend fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): List<RadioComponent> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getRadioComponentsByMatchesBoxId(matchesBoxId)
        }
    }

    override suspend fun getRadioComponentsByQuery(query: String): List<RadioComponent> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getRadioComponentsByQuery(query)
        }
    }

    override suspend fun getRadioComponentsToBuy(): List<RadioComponent> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getRadioComponentsToBuy()
        }
    }

    // DisplayQuantity

    override suspend fun getDisplayQuantityListBySetId(setId: Int): List<QuantityItemModel> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getDisplayQuantityListBySetId(setId)
        }
    }

    override suspend fun getDisplayQuantityListByBagId(bagId: Int): List<QuantityItemModel> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getDisplayQuantityListByBagId(bagId)
        }
    }

    override fun getBagsQuantityPresentationList(): LiveData<List<QuantityItemModel>> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getBagsDisplayQuantityList()
        }
    }

    // RadioComponentDetails

    override suspend fun getRadioComponentDetailsById(componentId: Int): RadioComponentDetails {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getRadioComponentDetailsById(componentId)
        }
    }

    // History

    override suspend fun insertHistory(history: History) {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.insertHistory(history)
        }
    }

    override fun observeHistoryList(): LiveData<List<History>> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.observeHistoryList()
        }
    }

    override suspend fun getHistoryList(): List<History> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getHistoryList()
        }
    }

    override suspend fun getHistoryListByComponentId(id: Int): List<History> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getHistoryListByComponentId(id)
        }
    }

    override suspend fun deleteHistory(history: History) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.deleteHistory(history)
        }
    }

    override fun observeHistoryModel(): LiveData<List<HistoryModel>> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.observeHistoryModel()
        }
    }

    override suspend fun getHistoryById(id: Int): History? {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getHistoryById(id)
        }
    }

    override fun observeHistoryListByComponentId(id: Int): LiveData<List<History>> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.observeHistoryListByComponentId(id)
        }
    }

    override suspend fun clearDatabase() {
        radioComponentsDatabaseDao.deleteAllHistory()
        radioComponentsDatabaseDao.deleteAllComponents()
        radioComponentsDatabaseDao.deleteAllBoxes()
        radioComponentsDatabaseDao.deleteAllSets()
        radioComponentsDatabaseDao.deleteAllBags()
    }
}