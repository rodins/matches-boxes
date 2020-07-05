package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FakeDataSource : RadioComponentsDataSource{
    private val bagsList = mutableListOf<Bag>()
    private val matchesBoxSetList = mutableListOf<MatchesBoxSet>()
    private val matchesBoxList = mutableListOf<MatchesBox>()
    private val radioComponentsList = mutableListOf<RadioComponent>()
    private val radioComponentsCountLiveData = MutableLiveData<Int>()

    // Bags
    fun addBags(vararg bags: Bag) {
        for(bag in bags) {
            bagsList.add(bag)
        }
    }

    override suspend fun insertBag(bag: Bag) {
        if(bag.id == 0) {
            bagsList.add(bag)
        }
    }

    override suspend fun updateBag(bag: Bag) {
        val index = bagsList.indexOfFirst {
            it.id == bag.id
        }
        bagsList[index] = bag
    }

    override suspend fun deleteBag(bag: Bag) {
        bagsList.remove(bag)
    }

    override suspend fun getBagById(bagId: Int): Bag? {
        return bagsList.find{
            it.id == bagId
        }
    }

    override suspend fun getBags(): List<Bag> {
        return bagsList
    }

    // MatchesBoxSet
    fun addMatchesBoxSets(vararg sets: MatchesBoxSet) {
        for(set in sets) {
            matchesBoxSetList.add(set)
        }
    }

    override suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet) {
        if(matchesBoxSet.id == 0) {
            matchesBoxSetList.add(matchesBoxSet)
        }
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

    override suspend fun getMatchesBoxSetsByBagId(bagId: Int): List<MatchesBoxSet> {
        return matchesBoxSetList.filter {
            it.bagId == bagId
        }
    }

    // MatchesBox
    fun addMatchesBoxes(vararg matchesBoxes: MatchesBox) {
        for(box in matchesBoxes) {
            matchesBoxList.add(box)
        }
    }

    override suspend fun insertMatchesBox(matchesBox: MatchesBox) {
        if(matchesBox.id == 0) {
            matchesBoxList.add(matchesBox)
        }
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

    override suspend fun getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId: Int): List<MatchesBox> {
        return matchesBoxList.filter {
            it.matchesBoxSetId == matchesBoxSetId
        }
    }

    // RadioComponent
    fun addRadioComponents(vararg components: RadioComponent) {
        for(component in components) {
            radioComponentsList.add(component)
        }
        radioComponentsCountLiveData.value = radioComponentsList.size
    }

    override suspend fun insertRadioComponent(radioComponent: RadioComponent) {
        if(radioComponent.id == 0) {
            radioComponentsList.add(radioComponent)
            radioComponentsCountLiveData.value = radioComponentsList.size
        }
    }

    override suspend fun updateRadioComponent(radioComponent: RadioComponent) {
        val index = radioComponentsList.indexOfFirst {
            it.id == radioComponent.id
        }
        radioComponentsList[index] = radioComponent
        radioComponentsCountLiveData.value = radioComponentsList.size
    }

    override suspend fun deleteRadioComponent(radioComponent: RadioComponent) {
        radioComponentsList.remove(radioComponent)
        radioComponentsCountLiveData.value = radioComponentsList.size
    }

    override suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent? {
        return radioComponentsList.find {
            it.id == radioComponentId
        }
    }

    override suspend fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): List<RadioComponent> {
        return radioComponentsList.filter {
            it.matchesBoxId == matchesBoxId
        }
    }

    override suspend fun getRadioComponentsSumQuantityByMatchesBoxId(matchesBoxId: Int): Int {
        var sum = 0
        radioComponentsList.forEach { component ->
            sum += component.quantity
        }
        return sum
    }

    override suspend fun getRadioComponentsByQuery(query: String): List<RadioComponent> {
        return radioComponentsList.filter{
            it.name.contains(query, true)
        }
    }

    override suspend fun getRadioComponentsToBuy(): List<RadioComponent> {
        return radioComponentsList.filter{
            it.isBuy
        }
    }

    override fun getRadioComponentsCount(): LiveData<Int> {
        return radioComponentsCountLiveData
    }
}