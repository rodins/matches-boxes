package com.sergeyrodin.matchesboxes.bag.list

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
import com.sergeyrodin.matchesboxes.data.QuantityItemModel
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.getOrAwaitValue
import com.sergeyrodin.matchesboxes.quantityitem.QuantityItem
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import net.bytebuddy.implementation.FixedValue.nullValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class BagsScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
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
    fun bagsScreen_bagsNamesDisplayed() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")

        dataSource.addBags(bag1, bag2)
        val viewModel = BagsListViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                BagsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(bag1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(bag2.name).assertIsDisplayed()
    }

    @Test
    fun displayQuantityItem_bagImageDisplayed() {
        val bag = QuantityItemModel(1, "Bag 1", "5")

        composeTestRule.setContent {
            AppCompatTheme {
                QuantityItem(bag.name, bag.componentsQuantity) {
                    BagIcon()
                }
            }
        }

        val bagIconDescription = composeTestRule.activity.getString(R.string.bag_icon_description)
        composeTestRule.onNodeWithContentDescription(bagIconDescription).assertIsDisplayed()
    }

    @Test
    fun bagsScreen_displayEmptyState() {
        dataSource.addBags()
        val viewModel = BagsListViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                BagsScreen(viewModel)
            }
        }

        val bagsEmptyText = composeTestRule.activity.getString(R.string.no_bags_added)
        composeTestRule.onNodeWithText(bagsEmptyText).assertIsDisplayed()
    }

    @Test
    fun itemClick_bagIdEquals() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")

        dataSource.addBags(bag1, bag2)
        val viewModel = BagsListViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                BagsScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithText(bag2.name).performClick()

        val bag = viewModel.selectBagEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(bag?.id, `is`(bag2.id))
    }

    @Test
    fun addBagFabClick_eventNotNull() {
        dataSource.addBags()
        val viewModel = BagsListViewModel(dataSource)

        composeTestRule.setContent {
            AppCompatTheme {
                BagsScreen(viewModel)
            }
        }
        val addBagDescription = composeTestRule.activity.getString(R.string.add_bag)
        composeTestRule.onNodeWithContentDescription(addBagDescription).performClick()

        val event = viewModel.addBagEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }
}
