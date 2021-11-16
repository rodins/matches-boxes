package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.R

import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
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
class BagScreenTest {
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
    fun addBag_hintDisplayed() {
        val viewModel = BagManipulatorViewModel(dataSource)
        viewModel.start(ADD_NEW_ITEM_ID)

        composeTestRule.setContent {
            AppCompatTheme {
                BagScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTextResource(R.string.enter_bag_name).assertIsDisplayed()
    }

    @Test
    fun editBag_nameDisplayed() {
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)

        val viewModel = BagManipulatorViewModel(dataSource)
        viewModel.start(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BagScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(bag.name).assertIsDisplayed()
    }

    @Test
    fun editBag_newNameDisplayed() {
        val updatedBagName = "Bag updated"

        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)

        val viewModel = BagManipulatorViewModel(dataSource)
        viewModel.start(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BagScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(bag.name).performTextReplacement(updatedBagName)

        composeTestRule.onNodeWithText(updatedBagName).assertIsDisplayed()
    }

    @Test
    fun editBag_nameEquals() {
        val updatedBagName = "Bag updated"
        val bag = Bag(1, "Bag")
        dataSource.addBags(bag)

        val viewModel = BagManipulatorViewModel(dataSource)
        viewModel.start(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BagScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(bag.name).performTextReplacement(updatedBagName)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_bag).performClick()

        val updatedBag = runBlocking { dataSource.getBags()[0] }
        assertThat(updatedBag.name, `is`(updatedBagName))
    }

    @Test
    fun addBag_nameEquals() {
        val newBagName = "New bag"
        val viewModel = BagManipulatorViewModel(dataSource)
        viewModel.start(ADD_NEW_ITEM_ID)

        composeTestRule.setContent {
            AppCompatTheme {
                BagScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTag(NAME_TEXT_FIELD_TAG).performTextInput(newBagName)
        composeTestRule.onNodeWithContentDescriptionResource(R.string.save_bag).performClick()

        val bag = runBlocking { dataSource.getBags()[0] }
        assertThat(bag.name, `is`(newBagName))
    }
}
