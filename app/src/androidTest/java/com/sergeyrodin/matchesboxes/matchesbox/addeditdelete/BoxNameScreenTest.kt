package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.nametextfield.NAME_TEXT_FIELD_TAG
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class BoxNameScreenTest {
    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun addBox_hintDisplayed() {
        val viewModel = MatchesBoxManipulatorViewModel(dataSource)
        viewModel.start(1, -1)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTextResource(R.string.enter_box_name).assertIsDisplayed()
    }

    @Test
    fun editBox_nameDisplayed() {
        val setId = 1
        val box = MatchesBox(1, "Box", setId)

        dataSource.addMatchesBoxes(box)
        val viewModel = MatchesBoxManipulatorViewModel(dataSource)
        viewModel.start(setId, box.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(box.name).assertIsDisplayed()
    }

    @Test
    fun editBox_nameEquals() {
        val boxNameUpdated = "Box updated"
        val setId = 1
        val box = MatchesBox(1, "Box", setId)

        dataSource.addMatchesBoxes(box)
        val viewModel = MatchesBoxManipulatorViewModel(dataSource)
        viewModel.start(setId, box.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement(boxNameUpdated)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        val boxUpdated = runBlocking { dataSource.getMatchesBoxById(box.id) }
        assertThat(boxUpdated?.name, `is`(boxNameUpdated))
    }

    @Test
    fun addBox_nameEquals() {
        val newBoxName = "New box"

        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)

        val viewModel = MatchesBoxManipulatorViewModel(dataSource)
        viewModel.start(set.id, -1)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput(newBoxName)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        val newName = runBlocking { dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)[0] }
        assertThat(newName.name, `is`(newBoxName))
    }
}
