package com.sergeyrodin.matchesboxes

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponentsDatabase
import com.sergeyrodin.matchesboxes.data.RadioComponentsDatabaseDao
import com.sergeyrodin.matchesboxes.data.RealDataSource

object ServiceLocator {
    @Volatile
    private var database: RadioComponentsDatabase? = null

    @Volatile
    var radioComponentsDataSource: RadioComponentsDataSource? = null
        @VisibleForTesting set

    private val lock = Any()

    fun provideRadioComponentsDataSource(context: Context): RadioComponentsDataSource {
        synchronized(this) {
            return radioComponentsDataSource ?: createRadioComponentsDataSource(context)
        }
    }

    private fun createRadioComponentsDataSource(context: Context): RadioComponentsDataSource {
        val newDataSource = RealDataSource(createRadioComponentsDatabaseDao(context))
        radioComponentsDataSource = newDataSource
        return newDataSource
    }

    private fun createRadioComponentsDatabaseDao(context: Context): RadioComponentsDatabaseDao {
        val componentsDatabase = database ?: createDatabase(context)
        return componentsDatabase.radioComponentsDatabaseDao
    }

    private fun createDatabase(context: Context): RadioComponentsDatabase {
        val componentsDatabase = Room.databaseBuilder(
            context, RadioComponentsDatabase::class.java, "radio_components"
        )
            .addMigrations(RadioComponentsDatabase.MIGRATION_1_2)
            .build()
        database = componentsDatabase
        return componentsDatabase
    }

    @VisibleForTesting
    fun resetDataSource() {
        synchronized(lock) {
            database?.apply {
                clearAllTables()
                close()
            }

            database = null
            radioComponentsDataSource = null
        }
    }
}
