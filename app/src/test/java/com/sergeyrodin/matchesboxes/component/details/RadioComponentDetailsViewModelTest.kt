package com.sergeyrodin.matchesboxes.component.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class RadioComponentDetailsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: RadioComponentDetailsViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = RadioComponentDetailsViewModel(dataSource)
    }



}