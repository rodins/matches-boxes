package com.sergeyrodin.matchesboxes.util

import com.sergeyrodin.matchesboxes.R

private var highlightedId: Int = -1

fun setHighlightedId(id: Int) {
    highlightedId = id
}

fun resetAndReturnHighlightedId(): Int {
    val prevId = highlightedId
    highlightedId = -1
    return prevId
}

fun isHighlighted(): Boolean {
    return highlightedId != -1
}

fun isNotHighlighted(): Boolean {
    return highlightedId == -1
}

fun getHighlightedBackgroundById(id: Int): Int {
    return if(highlightedId == id) {
        R.color.secondaryLightColor
    } else {
        R.color.design_default_color_background
    }
}