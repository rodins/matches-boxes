package com.sergeyrodin.matchesboxes.history

data class HistoryPresentation(
    var id: Int,
    var title: String,
    var quantity: String,
    var subTitle: String = "",
    var delta: String = "",
    var isHighlighted: Boolean = false
)