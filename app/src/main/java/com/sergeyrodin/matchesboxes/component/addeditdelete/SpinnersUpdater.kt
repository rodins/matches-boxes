package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class SpinnersUpdater(private val dataSource: RadioComponentsDataSource) {
    private val tag = javaClass.simpleName
    private var _matchesBoxId = 0
    val matchesBoxId: Int
        get() = _matchesBoxId

    private var currentBagSelectedIndex = NO_INDEX_SET
    private lateinit var bags: List<Bag>
    private val observableBags = MutableLiveData<List<Bag>>()
    val bagNames = observableBags.map {
        it.map { bag ->
            bag.name
        }
    }
    val noBagsTextVisible = bagNames.map {
        it.isEmpty()
    }

    private val selectedBagId = MutableLiveData<Int>()
    val bagSelectedIndex = selectedBagId.map { bagId ->
        getBagIndexById(bagId)
    }

    private val observableSets = MutableLiveData<List<MatchesBoxSet>>()
    val setNames = observableSets.map {
        it.map { set ->
            set.name
        }
    }
    val noSetsTextVisible = setNames.map {
        it.isEmpty()
    }
    private val selectedSetId = MutableLiveData<Int>()
    val setSelectedIndex = selectedSetId.switchMap { setId ->
        observableSets.map { setsList ->
            setsList.indexOfFirst { set ->
                set.id == setId
            }
        }
    }

    private val observableBoxes = MutableLiveData<List<MatchesBox>>()
    val boxNames = observableBoxes.map {
        it.map { box ->
            box.name
        }
    }
    val noBoxesTextVisible = boxNames.map {
        it.isEmpty()
    }
    private val selectedBoxId = MutableLiveData<Int>()
    val boxSelectedIndex = selectedBoxId.switchMap { boxId ->
        observableBoxes.map { boxesList ->
            boxesList.indexOfFirst { box ->
                box.id == boxId
            }
        }
    }

    suspend fun start(boxId: Int) {
        Log.i(tag, "start called: boxId = $boxId")
        _matchesBoxId = boxId
        initBags()
        if (boxId != NO_ID_SET) {
            updateSpinnersByBoxId(boxId)
        } else {
            updateSpinnersByBagId(getFirstBagId())
        }
    }

    private suspend fun initBags() {
        bags = dataSource.getBags()
    }

    private suspend fun updateSpinnersByBoxId(boxId: Int) {
        val box = getMatchesBoxFromDbById(boxId)
        val set = getMatchesBoxSetParentOfBox(box)
        val bagId = set?.bagId ?: NO_ID_SET
        val setId = set?.id ?: NO_ID_SET
        getSetsFromDb(bagId)
        getMatchesBoxesFromDb(setId)
        updateBagsSpinner(bagId)
        updateSelectedIds(bagId, setId, boxId)
    }

    private suspend fun getMatchesBoxFromDbById(boxId: Int): MatchesBox? {
        return dataSource.getMatchesBoxById(boxId)
    }

    private suspend fun getMatchesBoxSetParentOfBox(box: MatchesBox?): MatchesBoxSet? {
        val set = dataSource.getMatchesBoxSetById(box?.matchesBoxSetId ?: NO_ID_SET)
        return set
    }

    private suspend fun getSetsFromDb(bagId: Int) {
        observableSets.value = dataSource.getMatchesBoxSetsByBagId(bagId)
    }

    private suspend fun getMatchesBoxesFromDb(setId: Int) {
        observableBoxes.value = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
    }

    private fun updateBagsSpinner(bagId: Int) {
        currentBagSelectedIndex = getBagIndexById(bagId)?: NO_INDEX_SET
        Log.i(tag, "currentBagSelectedIndex = $currentBagSelectedIndex")
        observableBags.value = bags
    }

    private fun getBagIndexById(bagId: Int?): Int? {
        return bags.indexOfFirst { bag ->
            bag.id == bagId
        }
    }

    private fun updateSelectedIds(bagId: Int, setId: Int, boxId: Int) {
        selectedBagId.value = bagId
        selectedSetId.value = setId
        selectedBoxId.value = boxId
    }

    private suspend fun updateSpinnersByBagId(bagId: Int) {
        getSetsFromDb(bagId)
        val firstSetId = getFirstSetId()
        getMatchesBoxesFromDb(firstSetId)
        setMatchesBoxIdToFirstElement()
        updateBagsSpinner(bagId)
        updateSelectedIds(bagId, firstSetId, _matchesBoxId)
    }

    private fun getFirstSetId(): Int {
        return if (observableSets.value?.isNotEmpty() == true) {
            observableSets.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        }
    }

    private fun setMatchesBoxIdToFirstElement() {
        _matchesBoxId = if (observableBoxes.value?.isNotEmpty() == true) {
            observableBoxes.value?.get(0)?.id ?: NO_ID_SET
        } else {
            NO_ID_SET
        }
    }

    private fun getFirstBagId(): Int {
        return if (bags.isNotEmpty()) {
            bags[FIRST_INDEX].id
        } else {
            NO_ID_SET
        }
    }

    fun boxSelected(index: Int) {
        convertIndexToBoxId(index)
    }

    private fun convertIndexToBoxId(index: Int) {
        _matchesBoxId = observableBoxes.value?.get(index)?.id ?: NO_ID_SET
    }

    suspend fun setSelected(newIndex: Int) {
        if (newIndex != setSelectedIndex.value) {
            updateSpinnersBySetId(getSetIdByIndex(newIndex))
        }
    }

    private suspend fun updateSpinnersBySetId(setId: Int) {
        getMatchesBoxesFromDb(setId)
        setMatchesBoxIdToFirstElement()
        updateSelectedIds(setId)
    }

    private fun updateSelectedIds(setId: Int) {
        selectedSetId.value = setId
        selectedBoxId.value = _matchesBoxId
    }

    private fun getSetIdByIndex(newIndex: Int): Int {
        return observableSets.value?.get(newIndex)?.id ?: NO_ID_SET
    }

    suspend fun bagSelected(newIndex: Int) {
        Log.i(tag, "bag selected called: newIndex = $newIndex, currentSelectedIndex = $currentBagSelectedIndex")
        if (newIndex != currentBagSelectedIndex) {
            currentBagSelectedIndex = newIndex
            updateSpinnersByBagId(getBagIdByIndex(newIndex))
        }
    }

    private fun getBagIdByIndex(newIndex: Int): Int {
        return observableBags.value?.get(newIndex)?.id ?: NO_ID_SET
    }
}