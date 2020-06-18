package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData

interface RadioComponentsDataSource {

    //Bag
    suspend fun insertBag(bag: Bag)

    suspend fun updateBag(bag: Bag)

    suspend fun deleteBag(bag: Bag)

    suspend fun getBagById(bagId: Int): Bag?

    fun getBags(): LiveData<List<Bag>>

    // MatchesBoxSet
    suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    suspend fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    suspend fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    suspend fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet?

    suspend fun getMatchesBoxSetsByBagId(bagId: Int): List<MatchesBoxSet>

    // MatchesBox
    suspend fun insertMatchesBox(matchesBox: MatchesBox)

    suspend fun updateMatchesBox(matchesBox: MatchesBox)

    suspend fun deleteMatchesBox(matchesBox: MatchesBox)

    suspend fun getMatchesBoxById(matchesBoxId: Int): MatchesBox?

    suspend fun getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId: Int): List<MatchesBox>

    // RadioComponents
    suspend fun insertRadioComponent(radioComponent: RadioComponent)

    suspend fun updateRadioComponent(radioComponent: RadioComponent)

    suspend fun deleteRadioComponent(radioComponent: RadioComponent)

    suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent?

    suspend fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): List<RadioComponent>

    suspend fun getRadioComponentsByQuery(query: String): List<RadioComponent>

    suspend fun getRadioComponentsToBuy(): List<RadioComponent>

}