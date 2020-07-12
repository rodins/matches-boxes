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
    suspend fun getBags(): List<Bag>

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

    @Query("SELECT * FROM radio_components WHERE buy = 1")
    suspend fun getRadioComponentsToBuy(): List<RadioComponent>

    // DisplayQuantity

    @Query("SELECT b.id, b.name, TOTAL(quantity) as componentsQuantity FROM matches_boxes b LEFT JOIN radio_components ON matches_box_id = b.id GROUP BY b.id HAVING matches_box_set_id = :setId")
    suspend fun getDisplayQuantityListBySetId(setId: Int): List<DisplayQuantity>

    @Query("SELECT s.id, s.name, TOTAL(quantity) as componentsQuantity FROM matches_box_sets s LEFT JOIN matches_boxes b ON matches_box_set_id = s.id LEFT JOIN radio_components c ON matches_box_id = b.id GROUP BY s.id HAVING bag_id = :bagId")
    suspend fun getDisplayQuantityListByBagId(bagId: Int): List<DisplayQuantity>

    @Query("SELECT bags.id, bags.name, TOTAL(quantity) as componentsQuantity FROM bags LEFT JOIN matches_box_sets s ON  bag_id = bags.id LEFT JOIN matches_boxes b ON matches_box_set_id = s.id LEFT JOIN radio_components r ON matches_box_id = b.id GROUP BY bags.id")
    fun getBagsDisplayQuantityList(): LiveData<List<DisplayQuantity>>

    // RadioComponentDetails

    @Query("SELECT b.name as bagName, s.name as setName, box.name as boxName, c.name as componentName, c.quantity as componentQuantity FROM bags b INNER JOIN matches_box_sets s ON bag_id = b.id INNER JOIN matches_boxes box ON matches_box_set_id = s.id INNER JOIN radio_components c ON matches_box_id = box.id GROUP BY c.id HAVING c.id = :componentId")
    suspend fun getRadioComponentDetailsById(componentId: Int): RadioComponentDetails
}