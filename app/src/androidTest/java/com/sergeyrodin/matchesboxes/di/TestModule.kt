package com.sergeyrodin.matchesboxes.di

import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class TestModule {

    @Singleton
    @Binds
    abstract fun bindDataSource(dataSource: FakeDataSource): RadioComponentsDataSource
}
