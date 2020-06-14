package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RadioComponentsDatabaseDao {
    //Bag
    @Insert
    suspend fun insertBag(bag: Bag)

    @Update
    suspend fun updateBag(bag: Bag)

    @Delete
    suspend fun deleteBag(bag: Bag)

    @Query("SELECT * FROM bags WHERE id = :bagId")
    suspend fun getBagById(bagId: Int): Bag

    @Query("SELECT * FROM bags")
    fun getBags(): LiveData<List<Bag>>

    // MatchesBoxSet
    @Insert
    suspend fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    @Update
    suspend fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    @Delete
    suspend fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    @Query("SELECT * FROM matches_box_sets WHERE id = :matchesBoxSetId")
    suspend fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet

    @Query("SELECT * FROM matches_box_sets WHERE bag_id = :bagId")
    suspend fun getMatchesBoxSetsByBagId(bagId: Int): List<MatchesBoxSet>

    // MatchesBox
    @Insert
    suspend fun insertMatchesBox(matchesBox: MatchesBox)

    @Update
    suspend fun updateMatchesBox(matchesBox: MatchesBox)

    @Delete
    suspend fun deleteMatchesBox(matchesBox: MatchesBox)

    @Query("SELECT * FROM matches_boxes WHERE id = :matchesBoxId")
    suspend fun getMatchesBoxById(matchesBoxId: Int): MatchesBox

    @Query("SELECT * FROM matches_boxes WHERE matches_box_set_id = :matchesBoxSetId")
    suspend fun getMatchesBoxesByMatchesBoxSetId(matchesBoxSetId: Int): List<MatchesBox>

    // RadioComponents
    @Insert
    suspend fun insertRadioComponent(radioComponent: RadioComponent)

    @Update
    suspend fun updateRadioComponent(radioComponent: RadioComponent)

    @Delete
    suspend fun deleteRadioComponent(radioComponent: RadioComponent)

    @Query("SELECT * FROM radio_components WHERE id = :radioComponentId")
    suspend fun getRadioComponentById(radioComponentId: Int): RadioComponent

    @Query("SELECT * FROM radio_components WHERE matches_box_id = :matchesBoxId")
    suspend fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): List<RadioComponent>

    @Query("SELECT * FROM radio_components WHERE name LIKE '%' || :query || '%'")
    suspend fun getRadioComponentsByQuery(query: String): List<RadioComponent>
}