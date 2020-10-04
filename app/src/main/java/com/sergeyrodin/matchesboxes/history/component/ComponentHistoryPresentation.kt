package com.sergeyrodin.matchesboxes.history.component

data class ComponentHistoryPresentation(
    var id: Int,
    var date: String,
    var quantity: String,
    var delta: String = "",
    var isHighlighted: Boolean = false
)