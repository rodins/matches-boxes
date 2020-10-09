package com.sergeyrodin.matchesboxes.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import java.lang.IllegalArgumentException
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.sortedByDescending
import kotlin.collections.withIndex

class PopularComponentsViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    val popularItems = liveData {
        val historyItems = getHistoryItemsFromDb()
        val componentIdCountItems = mapComponentIdToCount(historyItems)
        val popularityItems = sortComponentIdByPopularity(componentIdCountItems)
        val presentationItems = convertPopularityToPresentation(popularityItems)
        emit(presentationItems)
    }

    private suspend fun getHistoryItemsFromDb(): List<History> {
        return dataSource.getHistoryList()
    }

    private fun mapComponentIdToCount(historyItems: List<History>): MutableMap<Int, Int> {
        val componentIdCountItems = mutableMapOf<Int, Int>()
        historyItems.forEach { history ->
            insertOrIncrementComponentId(componentIdCountItems, history.componentId)
        }
        return componentIdCountItems
    }

    private fun insertOrIncrementComponentId(
        componentIdCountItems: MutableMap<Int, Int>,
        componentId: Int
    ) {
        if (!componentIdCountItems.containsKey(componentId)) {
            componentIdCountItems[componentId] = 1
        } else {
            incrementCount(componentIdCountItems, componentId)
        }
    }

    private fun incrementCount(
        componentIdCountItems: MutableMap<Int, Int>,
        componentId: Int
    ) {
        val count = componentIdCountItems[componentId]
        componentIdCountItems[componentId] = count?.plus(1) ?: 0
    }

    private fun sortComponentIdByPopularity(componentIdCountItems: MutableMap<Int, Int>): List<ComponentPopularity> {
        return componentIdCountItems.map {
            ComponentPopularity(it.key, it.value)
        }.sortedByDescending {
            it.count
        }
    }

    private suspend fun convertPopularityToPresentation(popularityItems: List<ComponentPopularity>): MutableList<PopularPresentation> {
        val maxNumberOfItems = 20
        val presentationItems = mutableListOf<PopularPresentation>()
        for ((index, item) in popularityItems.withIndex()) {
            if(index >= maxNumberOfItems) break
            val popularPresentation = createPresentation(index, item.id)
            presentationItems.add(popularPresentation)
        }
        return presentationItems
    }

    private suspend fun createPresentation(
        index: Int,
        id: Int
    ): PopularPresentation {
        return PopularPresentation(getPlace(index), getName(id))
    }

    private fun getPlace(index: Int): String {
        return (index + 1).toString()
    }

    private suspend fun getName(id: Int): String {
        val component = dataSource.getRadioComponentById(id)
        return component?.name ?: ""
    }
}

data class PopularPresentation(
    var place: String,
    var name: String
)

data class ComponentPopularity(
    var id: Int,
    var count: Int
)

class PopularComponentsViewModelFactory(private val dataSource: RadioComponentsDataSource): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PopularComponentsViewModelFactory::class.java)) {
            return PopularComponentsViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("No view model class found.")
        }
    }
}