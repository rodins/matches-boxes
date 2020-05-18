package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FakeDataSource : RadioComponentsDataSource{
    private val bagsList = mutableListOf<Bag>()
    private val bagsLiveData = MutableLiveData<List<Bag>>()

    private val matchesBoxSetList = mutableListOf<MatchesBoxSet>()
    private val matchesBoxSetLiveData = MutableLiveData<List<MatchesBoxSet>>()

    private val matchesBoxList = mutableListOf<MatchesBox>()
    private val matchesBoxLiveData = MutableLiveData<List<MatchesBox>>()

    private val radioComponentsList = mutableListOf<RadioComponent>()
    private val radioComponentsLiveData = MutableLiveData<List<RadioComponent>>()

    // Bags
    fun addBags(vararg bags: Bag) {
        for(bag in bags) {
            bagsList.add(bag)
        }
        bagsLiveData.value = bagsList
    }

    override suspend fun insertBag(bag: Bag) {
        bagsList.add(bag)
        bagsLiveData.value = bagsList
    }

    override suspend fun updateBag(bag: Bag) {
        val index = bagsList.indexOfFirst {
            it.id == bag.id
        }
        bagsList[index] = bag
        bagsLiveData.value = bagsList
    }

    override suspend fun deleteBag(bag: Bag) {
        bagsList.remove(bag)
        bagsLiveData.value = bagsList
    }

    override suspend fun getBagById(bagId: Int): Bag? {
        return bagsList.find{
            it.id == bagId
        }
    }

    override fun getBags(): LiveData<List<Bag>> {
        return bagsLiveData
    }

    // MatchesBoxSet
    fun addMatchesBoxSets(vararg sets: MatchesBoxSet) {
        for(set in sets) {
            matchesBoxSetList.add(set)
        }
    }

    override suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        matchesBoxSetList.add(matchesBoxSet)
    }

    override suspend fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        val index = matchesBoxSetList.indexOfFirst {
            it.id == matchesBoxSet.id
        }
        matchesBoxSetList[index] = matchesBoxSet
    }

    override suspend fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        matchesBoxSetList.remove(matchesBoxSet)
    }

    override suspend fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet? {
        return matchesBoxSetList.find {
            it.id == matchesBoxSetId
        }
    }

    override fun getMatchesBoxSetsByBagId(bagId: Int): LiveData<List<MatchesBoxSet>> {
        matchesBoxSetLiveData.value = matchesBoxSetList.filter {
            it.bagId == bagId
        }
        return matchesBoxSetLiveData
    }

    // MatchesBox
    fun addMatchesBoxes(vararg matchesBoxes: MatchesBox) {
        for(box in matchesBoxes) {
            matchesBoxList.add(box)
        }
    }

    override suspend fun insertMatchesBox(matchesBox: MatchesBox) {
        matchesBoxList.add(matchesBox)
    }

    override suspend fun updateMatchesBox(matchesBox: MatchesBox) {
        val index = matchesBoxList.indexOfFirst {
            it.id == matchesBox.id
        }
        matchesBoxList[index] = matchesBox
    }

    override suspend fun deleteMatchesBox(matchesBox: MatchesBox) {
        matchesBoxList.remove(matchesBox)
    }

    override suspend fun getMatchesBoxById(matchesBoxId: Int): MatchesBox? {
        return matchesBoxList.find {
            it.id == matchesBoxId
        }
    }

    override fun getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId: Int): LiveData<List<MatchesBox>> {
        matchesBoxLiveData.value = matchesBoxList.filter {
            it.matchesBoxSetId == matchesBoxSetId
        }
        return matchesBoxLiveData
    }

    // RadioComponent
    fun addRadioComponents(vararg components: RadioComponent) {
        for(component in components) {
            radioComponentsList.add(component)
        }
    }

    override suspend fun insertRadioComponent(radioComponent: RadioComponent) {
        radioComponentsList.add(radioComponent)
    }

    override suspend fun updateRadioComponent(radioComponent: RadioComponent) {
        val index = radioComponentsList.indexOfFirst {
            it.id == radioComponent.id
        }
        radioComponentsList[index] = radioComponent
    }

    override suspend fun deleteRadioComponent(radioComponent: RadioComponent) {
        radioComponentsList.remove(radioComponent)
    }

    override suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent? {
        return radioComponentsList.find {
            it.id == radioComponentId
        }
    }

    override fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): LiveData<List<RadioComponent>> {
        radioComponentsLiveData.value = radioComponentsList.filter {
            it.matchesBoxId == matchesBoxId
        }
        return radioComponentsLiveData
    }
}