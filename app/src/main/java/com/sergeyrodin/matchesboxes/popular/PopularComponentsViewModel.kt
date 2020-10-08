package com.sergeyrodin.matchesboxes.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class PopularComponentsViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _popularItems = MutableLiveData<List<PopularPresentation>>()
    val popularItems: LiveData<List<PopularPresentation>>
        get() = _popularItems

    init {
        viewModelScope.launch{
            val historyItems = getHistoryItemsFromDb()
            val componentIdCountItems = mapComponentIdToCount(historyItems)
            val popularityItems = sortComponentIdByPopularity(componentIdCountItems)
            val presentationItems = convertPopularityToPresentation(popularityItems)
            updatePresentationItems(presentationItems)
        }
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
        val presentationItems = mutableListOf<PopularPresentation>()
        for ((index, item) in popularityItems.withIndex()) {
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

    private fun updatePresentationItems(presentationItems: MutableList<PopularPresentation>) {
        _popularItems.value = presentationItems
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