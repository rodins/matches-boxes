package com.sergeyrodin.matchesboxes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "matches_boxes",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = MatchesBoxSet::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("matches_box_set_id"),
            onDelete = CASCADE
        )
    )
)
data class MatchesBox(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    @ColumnInfo(name = "matches_box_set_id", index = true)
    var matchesBoxSetId: Int
)