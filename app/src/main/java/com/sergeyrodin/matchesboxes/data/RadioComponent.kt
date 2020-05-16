package com.sergeyrodin.matchesboxes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "radio_components",
    foreignKeys = arrayOf(
        ForeignKey(
        entity = MatchesBox::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("matches_box_id"),
        onDelete = CASCADE
        )
    )
)
data class RadioComponent(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1,
    var name: String,
    var quantity: Int = 0,
    @ColumnInfo(name = "matches_box_id", index = true)
    var matchesBoxId: Int
)