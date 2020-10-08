package com.sergeyrodin.matchesboxes.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class PopularComponentsViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {
    private val _popularItems = MutableLiveData<List<PopularPresentation>>()
    val popularItems: LiveData<List<PopularPresentation>>
        get() = _popularItems

    init {
        viewModelScope.launch{
            val historyItems = dataSource.getHistoryList()
            val componentIdCountItems = mutableMapOf<Int, Int>()
            val presentationItems = mutableListOf<PopularPresentation>()
            historyItems.forEach { history ->
                if(!componentIdCountItems.containsKey(history.componentId)) {
                    componentIdCountItems[history.componentId] = 1
                }else {
                    val count = componentIdCountItems[history.componentId]
                    componentIdCountItems[history.componentId] = count?.plus(1)?:0
                }

            }

            val popularityItems = componentIdCountItems.map {
                ComponentPopularity(it.key, it.value)
            }.sortedByDescending {
                it.count
            }

            for((index, item) in popularityItems.withIndex()) {
                val componentPlace = (index+1).toString()
                val component = dataSource.getRadioComponentById(item.id)
                val componentName = component?.name?:""
                val popularPresentation = PopularPresentation(componentPlace, componentName)
                presentationItems.add(popularPresentation)
            }

            _popularItems.value = presentationItems
        }
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