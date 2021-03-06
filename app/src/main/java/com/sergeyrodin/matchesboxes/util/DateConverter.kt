package com.sergeyrodin.matchesboxes.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy' 'HH:mm")
        .format(systemTime).toString()
}