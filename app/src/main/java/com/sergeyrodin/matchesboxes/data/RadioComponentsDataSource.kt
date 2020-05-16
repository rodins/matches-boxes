package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData

interface RadioComponentsDataSource {

    //Bag
    suspend fun insertBag(bag: Bag)

    suspend fun updateBag(bag: Bag)

    suspend fun deleteBag(bag: Bag)

    suspend fun getBagById(bagId: Int): Bag

    fun getBags(): LiveData<List<Bag>>

    // MatchesBoxSet
    suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    suspend fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    suspend fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    suspend fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet

    fun getMatchesBoxSetsByBagId(bagId: Int): LiveData<List<MatchesBoxSet>>

    // MatchesBox
    suspend fun insertMatchesBox(matchesBox: MatchesBox)

    suspend fun updateMatchesBox(matchesBox: MatchesBox)

    suspend fun deleteMatchesBox(matchesBox: MatchesBox)

    suspend fun getMatchesBoxById(matchesBoxId: Int): MatchesBox

    fun getMatchesBoxesFromMatchesBoxSetId(matchesBoxSetId: Int): LiveData<List<MatchesBox>>

    // RadioComponents
    suspend fun insertRadioComponent(radioComponent: RadioComponent)

    suspend fun updateRadioComponent(radioComponent: RadioComponent)

    suspend fun deleteRadioComponent(radioComponent: RadioComponent)

    suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent

    fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): LiveData<List<RadioComponent>>

}