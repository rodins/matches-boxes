package com.sergeyrodin.matchesboxes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "history",
    foreignKeys = [ForeignKey(
        entity = RadioComponent::class,
        parentColumns = ["id"],
        childColumns = ["component_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class History(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "component_id", index = true)
    var componentId: Int,
    var quantity: Int,
    var date: Long = System.currentTimeMillis()
)

