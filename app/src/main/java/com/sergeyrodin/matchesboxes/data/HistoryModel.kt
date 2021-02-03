package com.sergeyrodin.matchesboxes.data

data class HistoryModel(
    val id: Int,
    val componentId: Int,
    val name: String,
    val quantity: Int,
    val date: Long
)