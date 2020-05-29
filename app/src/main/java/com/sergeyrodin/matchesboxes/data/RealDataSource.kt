package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import com.sergeyrodin.matchesboxes.util.wrapEspressoIdlingResource

// This is only needed to be able to use fake data source in tests
class RealDataSource(private val radioComponentsDatabaseDao: RadioComponentsDatabaseDao) : RadioComponentsDataSource {
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

    override fun getBags(): LiveData<List<Bag>> {
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

    override suspend fun insertRadioComponent(radioComponent: RadioComponent) {
        wrapEspressoIdlingResource {
            radioComponentsDatabaseDao.insertRadioComponent(radioComponent)
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

    override fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): LiveData<List<RadioComponent>> {
        wrapEspressoIdlingResource {
            return radioComponentsDatabaseDao.getRadioComponentsByMatchesBoxId(matchesBoxId)
        }
    }
}