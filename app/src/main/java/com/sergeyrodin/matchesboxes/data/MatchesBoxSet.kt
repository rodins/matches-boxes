package com.sergeyrodin.matchesboxes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "matches_box_sets",
foreignKeys = arrayOf(ForeignKey(
    entity = Bag::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("bag_id"),
    onDelete = CASCADE
)))
data class MatchesBoxSet(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    @ColumnInfo(name = "bag_id", index = true)
    var bagId: Int
)
