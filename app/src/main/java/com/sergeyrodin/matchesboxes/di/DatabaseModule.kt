package com.sergeyrodin.matchesboxes.di

import android.content.Context
import androidx.room.Room
import com.sergeyrodin.matchesboxes.data.RadioComponentsDatabase
import com.sergeyrodin.matchesboxes.data.RadioComponentsDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RadioComponentsDatabase {
        return Room.databaseBuilder(
            appContext,
            RadioComponentsDatabase::class.java,
            "radio_components"
        )
            .addMigrations(RadioComponentsDatabase.MIGRATION_1_2, RadioComponentsDatabase.MIGRATION_2_3)
            .build()
    }


    @Provides
    fun provideDao(database: RadioComponentsDatabase): RadioComponentsDatabaseDao {
        return database.radioComponentsDatabaseDao
    }
}

