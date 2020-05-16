package com.sergeyrodin.matchesboxes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bags")
data class Bag(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String
)
