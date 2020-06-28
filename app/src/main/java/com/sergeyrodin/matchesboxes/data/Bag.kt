package com.sergeyrodin.matchesboxes.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "bags")
@Parcelize
data class Bag(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String
): Parcelable
