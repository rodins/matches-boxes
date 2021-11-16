package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
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
class BoxesScreenTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 3)
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun boxesList_namesDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box1 = MatchesBox(1, "Box1", set.id)
        val box2 = MatchesBox(2, "Box2", set.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box1, box2)

        val viewModel = MatchesBoxListViewModel(dataSource)
        viewModel.startBox(set.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxesScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(box1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(box2.name).assertIsDisplayed()
    }

    @Test
    fun boxesList_iconDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)

        val viewModel = MatchesBoxListViewModel(dataSource)
        viewModel.startBox(set.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxesScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithContentDescriptionResource(R.string.box_icon_description).assertIsDisplayed()
    }

    @Test
    fun boxesList_itemClick_itemIdEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box1 = MatchesBox(1, "Box1", set.id)
        val box2 = MatchesBox(2, "Box2", set.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box1, box2)

        val viewModel = MatchesBoxListViewModel(dataSource)
        viewModel.startBox(set.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxesScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(box2.name).performClick()

        val box = viewModel.selectBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(box?.id, `is`(box2.id))
    }

    @Test
    fun boxesList_displayEmptyState() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)

        val viewModel = MatchesBoxListViewModel(dataSource)
        viewModel.startBox(set.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxesScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTextResource(R.string.no_matches_boxes_added).assertIsDisplayed()
    }

    @Test
    fun addBoxFabClick_addBoxEventNotNull() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)

        val viewModel = MatchesBoxListViewModel(dataSource)
        viewModel.startBox(set.id)

        composeTestRule.setContent {
            AppCompatTheme {
                BoxesScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_box).performClick()

        val event = viewModel.addBoxEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }
}
