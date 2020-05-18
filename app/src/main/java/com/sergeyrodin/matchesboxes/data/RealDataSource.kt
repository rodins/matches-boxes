package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData

// This is only needed to be able to use fake data source in tests
class RealDataSource(private val radioComponentsDatabaseDao: RadioComponentsDatabaseDao) : RadioComponentsDataSource {
    override suspend fun insertBag(bag: Bag) {
        radioComponentsDatabaseDao.insertBag(bag)
    }

    override suspend fun updateBag(bag: Bag) {
        radioComponentsDatabaseDao.updateBag(bag)
    }

    override suspend fun deleteBag(bag: Bag) {
        radioComponentsDatabaseDao.deleteBag(bag)
    }

    override suspend fun getBagById(bagId: Int): Bag {
        return radioComponentsDatabaseDao.getBagById(bagId)
    }

    override fun getBags(): LiveData<List<Bag>> {
        return radioComponentsDatabaseDao.getBags()
    }

    override suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        radioComponentsDatabaseDao.insertMatchesBoxSet(matchesBoxSet)
    }

    override suspend fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        radioComponentsDatabaseDao.updateMatchesBoxSet(matchesBoxSet)
    }

    override suspend fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        radioComponentsDatabaseDao.deleteMatchesBoxSet(matchesBoxSet)
    }

    override suspend fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet {
        return radioComponentsDatabaseDao.getMatchesBoxSetById(matchesBoxSetId)
    }

    override fun getMatchesBoxSetsByBagId(bagId: Int): LiveData<List<MatchesBoxSet>> {
        return radioComponentsDatabaseDao.getMatchesBoxSetsByBagId(bagId)
    }

    override suspend fun insertMatchesBox(matchesBox: MatchesBox) {
        radioComponentsDatabaseDao.insertMatchesBox(matchesBox)
    }

    override suspend fun updateMatchesBox(matchesBox: MatchesBox) {
        radioComponentsDatabaseDao.updateMatchesBox(matchesBox)
    }

    override suspend fun deleteMatchesBox(matchesBox: MatchesBox) {
        radioComponentsDatabaseDao.deleteMatchesBox(matchesBox)
    }

    override suspend fun getMatchesBoxById(matchesBoxId: Int): MatchesBox {
        return radioComponentsDatabaseDao.getMatchesBoxById(matchesBoxId)
    }

    override fun getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId: Int): LiveData<List<MatchesBox>> {
        return radioComponentsDatabaseDao.getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId)
    }

    override suspend fun insertRadioComponent(radioComponent: RadioComponent) {
        radioComponentsDatabaseDao.insertRadioComponent(radioComponent)
    }

    override suspend fun updateRadioComponent(radioComponent: RadioComponent) {
        radioComponentsDatabaseDao.updateRadioComponent(radioComponent)
    }

    override suspend fun deleteRadioComponent(radioComponent: RadioComponent) {
        radioComponentsDatabaseDao.deleteRadioComponent(radioComponent)
    }

    override suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent {
        return radioComponentsDatabaseDao.getRadioComponentById(radioComponentId)
    }

    override fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): LiveData<List<RadioComponent>> {
        return radioComponentsDatabaseDao.getRadioComponentsByMatchesBoxId(matchesBoxId)
    }
}