package com.sergeyrodin.matchesboxes.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RadioComponentsDatabaseDao {
    //Bag
    @Insert
    fun insertBag(bag: Bag)

    @Update
    fun updateBag(bag: Bag)

    @Delete
    fun deleteBag(bag: Bag)

    @Query("SELECT * FROM bags WHERE id = :bagId")
    fun getBagById(bagId: Int): Bag

    @Query("SELECT * FROM bags")
    fun getBags(): LiveData<List<Bag>>

    // MatchesBoxSet
    @Insert
    fun insertMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    @Update
    fun updateMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    @Delete
    fun deleteMatchesBoxSet(matchesBoxSet: MatchesBoxSet)

    @Query("SELECT * FROM matches_box_sets WHERE id = :matchesBoxSetId")
    fun getMatchesBoxSetById(matchesBoxSetId: Int): MatchesBoxSet

    @Query("SELECT * FROM matches_box_sets WHERE bag_id = :bagId")
    fun getMatchesBoxSetsByBagId(bagId: Int): LiveData<List<MatchesBoxSet>>

    // MatchesBox
    @Insert
    fun insertMatchesBox(matchesBox: MatchesBox)

    @Update
    fun updateMatchesBox(matchesBox: MatchesBox)

    @Delete
    fun deleteMatchesBox(matchesBox: MatchesBox)

    @Query("SELECT * FROM matches_boxes WHERE id = :matchesBoxId")
    fun getMatchesBoxById(matchesBoxId: Int): MatchesBox

    @Query("SELECT * FROM matches_boxes WHERE id = :matchesBoxSetId")
    fun getMatchesBoxesFromMatchesBoxSetId(matchesBoxSetId: Int): LiveData<List<MatchesBox>>

    // RadioComponents
    @Insert
    fun insertRadioComponent(radioComponent: RadioComponent)

    @Update
    fun updateRadioComponent(radioComponent: RadioComponent)

    @Delete
    fun deleteRadioComponent(radioComponent: RadioComponent)

    @Query("SELECT * FROM radio_components WHERE id = :radioComponentId")
    fun getRadioComponentById(radioComponentId: Int): RadioComponent

    @Query("SELECT * FROM radio_components WHERE matches_box_id = :matchesBoxId")
    fun getRadioComponentsByMatchesBoxId(matchesBoxId: Int): LiveData<List<RadioComponent>>
}