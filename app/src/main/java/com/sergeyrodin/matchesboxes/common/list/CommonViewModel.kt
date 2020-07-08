package com.sergeyrodin.matchesboxes.common.list

import androidx.lifecycle.*
import com.sergeyrodin.matchesboxes.Event
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import kotlinx.coroutines.launch

class CommonViewModel(private val dataSource: RadioComponentsDataSource): ViewModel() {

    // Bags
    val bagsList = dataSource.getBagsDisplayQuantityList()

    val isNoBagsTextVisible = Transformations.map(bagsList) {list ->
        list.isEmpty()
    }

    private val _addBagEvent = MutableLiveData<Event<Unit>>()
    val addBagEvent: LiveData<Event<Unit>>
        get() = _addBagEvent

    private val _selectBagEvent = MutableLiveData<Event<Int>>()
    val selectBagEvent: LiveData<Event<Int>>
        get() = _selectBagEvent

    fun addBag() {
        _addBagEvent.value = Event(Unit)
    }

    fun selectBag(id: Int) {
        _selectBagEvent.value = Event(id)
    }

    // Sets
    private val bagId = MutableLiveData<Int>()
    val setsList = bagId.switchMap{ id ->
        liveData{
            emit(dataSource.getDisplayQuantityListByBagId(id))
        }
    }

    val bagTitle = bagId.switchMap { id ->
        liveData<String> {
            val bag = dataSource.getBagById(id)
            emit(bag?.name?:"")
        }
    }

    val isNoSetsTextVisible = Transformations.map(setsList) { list ->
        list.isEmpty()
    }

    private val _addSetEvent = MutableLiveData<Event<Unit>>()
    val addSetEvent: LiveData<Event<Unit>>
        get() = _addSetEvent

    private val _selectSetEvent = MutableLiveData<Event<Int>>()
    val selectSetEvent: LiveData<Event<Int>>
        get() = _selectSetEvent

    fun startSet(id: Int) {
        bagId.value = id
    }

    fun addSet() {
        _addSetEvent.value = Event(Unit)
    }

    fun selectSet(id: Int) {
        _selectSetEvent.value = Event(id)
    }

    // Boxes

    private val matchesBoxSetId = MutableLiveData<Int>()
    val boxesList = matchesBoxSetId.switchMap{ setId ->
        liveData{
            emit(dataSource.getDisplayQuantityListBySetId(setId))
        }
    }

    val setTitle = matchesBoxSetId.switchMap { setId ->
        liveData<String>{
            val set = dataSource.getMatchesBoxSetById(setId)
            emit(set?.name?:"")
        }
    }

    val isNoBoxesTextVisible = Transformations.map(boxesList) {
        it.isEmpty()
    }

    private val _addBoxEvent = MutableLiveData<Event<Unit>>()
    val addBoxEvent: LiveData<Event<Unit>>
        get() = _addBoxEvent

    private val _selectBoxEvent = MutableLiveData<Event<MatchesBox>>()
    val selectBoxEvent: LiveData<Event<MatchesBox>>
        get() = _selectBoxEvent

    fun startBox(id: Int) {
        matchesBoxSetId.value = id
    }

    fun addBox() {
        _addBoxEvent.value = Event(Unit)
    }

    fun selectBox(id: Int) {
        viewModelScope.launch {
            val box = dataSource.getMatchesBoxById(id)
            _selectBoxEvent.value = Event(box!!)
        }
    }

    // Components

    private val boxId = MutableLiveData<Int>()
    val componentsList = boxId.switchMap{
        liveData{
            emit(dataSource.getRadioComponentsByMatchesBoxId(it))
        }
    }

    private val _addComponentEvent = MutableLiveData<Event<Unit>>()
    val addComponentEvent: LiveData<Event<Unit>>
        get() = _addComponentEvent

    val noComponentsTextVisible = Transformations.map(componentsList) { list ->
        list.isEmpty()
    }

    fun startComponent(id: Int) {
        boxId.value = id
    }

    fun addComponent() {
        _addComponentEvent.value = Event(Unit)
    }
}