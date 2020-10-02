package com.sergeyrodin.matchesboxes.history.all

data class HistoryPresentation(
    var id: Int,
    var componentId: Int,
    var name: String,
    var quantity: String,
    var date: String,
    var delta: String = "",
    var isHighlighted: Boolean = false
)