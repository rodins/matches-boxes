package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.MainActivity
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.nametextfield.NAME_TEXT_FIELD_TAG
import com.sergeyrodin.matchesboxes.onNodeWithContentDescriptionResource
import com.sergeyrodin.matchesboxes.onNodeWithTextResource
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
class SetNameScreenTest {
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
    fun addSet_hintDisplayed() {
        val viewModel = MatchesBoxSetManipulatorViewModel(dataSource)
        viewModel.start(1, -1)

        composeTestRule.setContent {
            AppCompatTheme {
                SetNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTextResource(R.string.enter_matches_box_set_name)
            .assertIsDisplayed()
    }

    @Test
    fun editSet_nameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)

        val viewModel = MatchesBoxSetManipulatorViewModel(dataSource)
        viewModel.start(bag.id, set.id)
        composeTestRule.setContent {
            AppCompatTheme {
                SetNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(set.name).assertIsDisplayed()
    }

    @Test
    fun editSet_nameEquals() {
        val newSetName = "Set updated"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)

        val viewModel = MatchesBoxSetManipulatorViewModel(dataSource)
        viewModel.start(bag.id, set.id)
        composeTestRule.setContent {
            AppCompatTheme {
                SetNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextReplacement(newSetName)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        val setUpdated = runBlocking { dataSource.getMatchesBoxSetById(set.id) }
        assertThat(setUpdated?.name, `is`(newSetName))
    }

    @Test
    fun addSet_nameEquals() {
        val newSetName = "Set updated"
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)

        val viewModel = MatchesBoxSetManipulatorViewModel(dataSource)
        viewModel.start(bag.id, -1)
        composeTestRule.setContent {
            AppCompatTheme {
                SetNameScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput(newSetName)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_name).performClick()

        val set = runBlocking { dataSource.getMatchesBoxSetsByBagId(bag.id)[0] }
        assertThat(set.name, `is`(newSetName))
    }
}