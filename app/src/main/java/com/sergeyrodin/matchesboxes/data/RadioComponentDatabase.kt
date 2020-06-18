package com.sergeyrodin.matchesboxes.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Bag::class, MatchesBoxSet::class, MatchesBox::class, RadioComponent::class],
    version = 2,
    exportSchema = false)
abstract class RadioComponentsDatabase : RoomDatabase(){
    abstract val radioComponentsDatabaseDao: RadioComponentsDatabaseDao
}