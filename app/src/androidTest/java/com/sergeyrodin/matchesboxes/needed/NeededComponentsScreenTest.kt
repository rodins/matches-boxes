package com.sergeyrodin.matchesboxes.needed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.data.*
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
class NeededComponentsScreenTest {

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
    fun componentsList_nameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3,  box.id, true)
        val component2 = RadioComponent(2, "Component2", 4,  box.id, true)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2)

        val viewModel = NeededComponentsViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                NeededComponentsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(component1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(component2.name).assertIsDisplayed()
    }

    @Test
    fun componentsList_iconDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3,  box.id, true)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1)

        val viewModel = NeededComponentsViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                NeededComponentsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithContentDescriptionResource(R.string.component_icon_description).assertIsDisplayed()
    }

    @Test
    fun componentsList_itemClick_itemIdEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "Component1", 3,  box.id, true)
        val component2 = RadioComponent(2, "Component2", 4,  box.id, true)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component1, component2)

        val viewModel = NeededComponentsViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                NeededComponentsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(component2.name).performClick()

        val componentId = viewModel.selectedComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(componentId, `is`(component2.id))
    }

    @Test
    fun componentsList_displayEmptyState() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)

        val viewModel = NeededComponentsViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                NeededComponentsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTextResource(R.string.no_components_found).assertIsDisplayed()
    }

    @Test
    fun addComponentFabClick_addComponentEventNotNull() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)

        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)

        val viewModel = NeededComponentsViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                NeededComponentsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithContentDescriptionResource(R.string.add_component).performClick()

        val event = viewModel.addComponentEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }
}