package com.sergeyrodin.matchesboxes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Bag::class, MatchesBoxSet::class, MatchesBox::class, RadioComponent::class],
    version = 2,
    exportSchema = true)
abstract class RadioComponentsDatabase : RoomDatabase(){
    abstract val radioComponentsDatabaseDao: RadioComponentsDatabaseDao

    companion object {
        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE radio_components ADD COLUMN buy INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}