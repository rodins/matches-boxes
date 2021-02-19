package com.sergeyrodin.matchesboxes.di

import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import com.sergeyrodin.matchesboxes.data.RealDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RadioComponentsDataSourceModule {

    @Singleton
    @Binds
    abstract fun provideDataSource(dataSource: RealDataSource): RadioComponentsDataSource
}