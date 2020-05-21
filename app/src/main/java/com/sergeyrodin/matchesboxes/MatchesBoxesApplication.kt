package com.sergeyrodin.matchesboxes

import android.app.Application
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource

class MatchesBoxesApplication : Application() {
    val radioComponentsDataSource: RadioComponentsDataSource
        get() = ServiceLocator.provideRadioComponentsDataSource(this)
}