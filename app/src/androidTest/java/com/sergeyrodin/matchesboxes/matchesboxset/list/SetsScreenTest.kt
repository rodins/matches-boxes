package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.MainActivity
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class SetsScreenTest {

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
    fun setsList_namesDisplayed() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2)
        val viewModel = MatchesBoxSetsListViewModel(dataSource)
        viewModel.startSet(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                SetsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(set1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(set2.name).assertIsDisplayed()
    }

    @Test
    fun setsList_iconDisplayed() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1)
        val viewModel = MatchesBoxSetsListViewModel(dataSource)
        viewModel.startSet(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                SetsScreen(viewModel)
            }
        }
        val setIconDescription = composeTestRule.activity.getString(R.string.set_icon_description)
        composeTestRule.onNodeWithContentDescription(setIconDescription).assertIsDisplayed()
    }

    @Test
    fun setsList_itemClick_itemIdEquals() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2)
        val viewModel = MatchesBoxSetsListViewModel(dataSource)
        viewModel.startSet(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                SetsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(set2.name).performClick()

        val set = viewModel.selectSetEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(set?.id, `is`(set2.id))
    }

    @Test
    fun setsList_displayEmptyState() {
        val bag = Bag(1, "Bag")

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets()
        val viewModel = MatchesBoxSetsListViewModel(dataSource)
        viewModel.startSet(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                SetsScreen(viewModel)
            }
        }

        val setsEmptyText = composeTestRule.activity.getString(R.string.no_matches_box_sets_added)
        composeTestRule.onNodeWithText(setsEmptyText).assertIsDisplayed()
    }

    @Test
    fun addSetFabClick_addSetEventNotNull() {
        val bag = Bag(1, "Bag")

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets()
        val viewModel = MatchesBoxSetsListViewModel(dataSource)
        viewModel.startSet(bag.id)

        composeTestRule.setContent {
            AppCompatTheme {
                SetsScreen(viewModel)
            }
        }

        val addSetDescription = composeTestRule.activity.getString(R.string.add_set)
        composeTestRule.onNodeWithContentDescription(addSetDescription).performClick()

        val event = viewModel.addSetEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }
}