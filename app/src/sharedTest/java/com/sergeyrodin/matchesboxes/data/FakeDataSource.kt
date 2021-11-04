package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDataSource @Inject constructor() : RadioComponentsDataSource{
    private val bagsList = mutableListOf<Bag>()
    private val observableBags = MutableLiveData<List<QuantityItemModel>>(listOf())
    private val matchesBoxSetList = mutableListOf<MatchesBoxSet>()
    private val matchesBoxList = mutableListOf<MatchesBox>()
    private val radioComponentsList = mutableListOf<RadioComponent>()
    private var radioComponentId = 0L
    private val historyList = mutableListOf<History>()
    private val observableHistoryList = MutableLiveData<List<History>>()

    private val observableHistoryModelList = observableHistoryList.map {
        it.map { history ->
            HistoryModel(
                history.id,
                history.componentId,
                getComponentName(history.componentId),
                history.quantity,
                history.date
            )
        }
    }

    private fun getComponentName(componentId: Int): String {
        return radioComponentsList.find {
            it.id == componentId
        }?.name ?: ""
    }

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
    }

    override suspend fun insertRadioComponent(radioComponent: RadioComponent): Long {
        if(radioComponent.id == 0) {
            radioComponent.id = (++radioComponentId).toInt()
            radioComponentsList.add(radioComponent)
            return radioComponentId
        }
        return 0L
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

    override suspend fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): List<RadioComponent> {
        return radioComponentsList.filter {
            it.matchesBoxId == matchesBoxId
        }
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

    override suspend fun getDisplayQuantityListByBoxId(boxId: Int): List<QuantityItemModel> {
        return radioComponentsList.filter {
            it.matchesBoxId == boxId
        }.map {
            QuantityItemModel(it.id, it.name, it.quantity.toString())
        }
    }

    override suspend fun getDisplayQuantityListBySetId(setId: Int): List<QuantityItemModel> {
        val output = mutableListOf<QuantityItemModel>()
        matchesBoxList.filter { box ->
            box.matchesBoxSetId == setId
        }.forEach { box ->
            var sum = 0
            radioComponentsList.filter { component ->
                component.matchesBoxId == box.id
            }.forEach { component ->
                sum += component.quantity
            }
            val displayQuantity = QuantityItemModel(box.id, box.name, sum.toString())
            output.add(displayQuantity)
        }
        return output
    }

    override suspend fun getDisplayQuantityListByBagId(bagId: Int): List<QuantityItemModel> {
        val output = mutableListOf<QuantityItemModel>()
        matchesBoxSetList.filter { set ->
            set.bagId == bagId
        }.forEach { set ->
            var sum = 0
            matchesBoxList.filter { box ->
                box.matchesBoxSetId == set.id
            }.forEach { box ->
                radioComponentsList.filter { component ->
                    component.matchesBoxId == box.id
                }.forEach { component ->
                    sum += component.quantity
                }
            }
            val displayQuantity = QuantityItemModel(set.id, set.name, sum.toString())
            output.add(displayQuantity)
        }
        return output
    }

    override fun getBagsQuantityPresentationList(): LiveData<List<QuantityItemModel>> {
        initBagsLiveData()
        return observableBags
    }

    override suspend fun getRadioComponentDetailsById(componentId: Int): RadioComponentDetails {
        val component = getRadioComponentById(componentId)
        val box = getMatchesBoxById(component?.matchesBoxId!!)
        val set = getMatchesBoxSetById(box?.matchesBoxSetId!!)
        val bag = getBagById(set?.bagId!!)
        return RadioComponentDetails(bag?.name!!, set.name, box.name, component.name, component.quantity.toString(), component.isBuy)
    }

    fun initBagsLiveData() {
        val list = mutableListOf<QuantityItemModel>()

        bagsList.forEach { bag ->
            var sum = 0
            matchesBoxSetList.filter { set ->
                set.bagId == bag.id
            }.forEach { set ->
                matchesBoxList.filter { box ->
                    box.matchesBoxSetId == set.id
                }.forEach { box ->
                    radioComponentsList.filter { component ->
                        component.matchesBoxId == box.id
                    }.forEach { component ->
                        sum += component.quantity
                    }
                }
            }
            val displayQuantity = QuantityItemModel(bag.id, bag.name, sum.toString())
            list.add(displayQuantity)
        }
        observableBags.value = list
    }

    // History

    fun addHistory(vararg histories: History) {
        for(history in histories) {
            historyList.add(history)
        }
        observableHistoryList.value = historyList.reversed()
    }

    override suspend fun insertHistory(history: History) {
        historyList.add(history)
        observableHistoryList.value = historyList.reversed()
    }

    override fun observeHistoryList(): LiveData<List<History>> {
        return observableHistoryList
    }

    override suspend fun getHistoryList(): List<History> {
        return historyList.reversed()
    }

    override suspend fun getHistoryListByComponentId(id: Int): List<History> {
        return historyList.reversed().filter {
            it.componentId == id
        }
    }

    override suspend fun deleteHistory(history: History) {
        historyList.remove(history)
        observableHistoryList.value = historyList.reversed()
    }

    override fun observeHistoryModel(): LiveData<List<HistoryModel>> {
        return observableHistoryModelList
    }

    override suspend fun getHistoryById(id: Int): History? {
        return historyList.find {
            it.id == id
        }
    }

    override fun observeHistoryListByComponentId(id: Int): LiveData<List<History>> {
        observableHistoryList.value = historyList.reversed().filter {
            it.componentId == id
        }
        return observableHistoryList
    }

    override suspend fun clearDatabase() {
        TODO("Not yet implemented")
    }
}