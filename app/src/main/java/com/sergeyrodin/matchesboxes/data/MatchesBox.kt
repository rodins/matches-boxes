package com.sergeyrodin.matchesboxes.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "matches_boxes",
    foreignKeys = [ForeignKey(
        entity = MatchesBoxSet::class,
        parentColumns = ["id"],
        childColumns = ["matches_box_set_id"],
        onDelete = CASCADE
    )]
)
@Parcelize
data class MatchesBox(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    @ColumnInfo(name = "matches_box_set_id", index = true)
    var matchesBoxSetId: Int
): Parcelable