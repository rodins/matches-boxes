package com.sergeyrodin.matchesboxes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Bag::class, MatchesBoxSet::class, MatchesBox::class, RadioComponent::class, History::class],
    version = 3,
    exportSchema = true)
abstract class RadioComponentsDatabase : RoomDatabase(){
    abstract val radioComponentsDatabaseDao: RadioComponentsDatabaseDao

    companion object {
        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE radio_components ADD COLUMN buy INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object: Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, component_id INTEGER NOT NULL, quantity INTEGER NOT NULL, date INTEGER NOT NULL, FOREIGN KEY(component_id) REFERENCES radio_components(id) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_history_component_id ON history(component_id)"
                )
            }
        }
    }
}