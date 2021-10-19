package com.sergeyrodin.matchesboxes.quantityitem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.MainActivity
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.QuantityItemModel
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class QuantityItemListScreenTest {
    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun quantityItemList_namesDisplayed() {

        val items = listOf(
            QuantityItemModel(1, "Item 1", "5"),
            QuantityItemModel(2, "Item 2", "6")
        )

        composeTestRule.setContent {
            AppCompatTheme {
                QuantityItemListScreen(items)
            }
        }

        composeTestRule.onNodeWithText(items[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(items[1].name).assertIsDisplayed()
    }

    @Test
    fun quantityItemList_quantitiesDisplayed() {
        val items = listOf(
            QuantityItemModel(1, "Item 1", "5"),
            QuantityItemModel(2, "Item 2", "6")
        )

        composeTestRule.setContent {
            AppCompatTheme {
                QuantityItemListScreen(items)
            }
        }

        val item1Quantity = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            items[0].componentsQuantity.toInt(),
            items[0].componentsQuantity
        )

        val item2Quantity = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            items[1].componentsQuantity.toInt(),
            items[1].componentsQuantity
        )

        composeTestRule.onNodeWithText(item1Quantity).assertIsDisplayed()
        composeTestRule.onNodeWithText(item2Quantity).assertIsDisplayed()
    }

    @Test
    fun quantityItem_textAndQuantityDisplayed() {
        val item = QuantityItemModel(1, "Item", "5")

        composeTestRule.setContent {
            AppCompatTheme {
                QuantityItem(item.name, item.componentsQuantity)
            }
        }

        val quantity = composeTestRule.activity.resources.getQuantityString(
            R.plurals.components_quantity,
            item.componentsQuantity.toInt(),
            item.componentsQuantity
        )

        composeTestRule.onNodeWithText(item.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(quantity).assertIsDisplayed()
    }

    @Test
    fun itemsIsNull_loadingIndicatorDisplayed() {
        composeTestRule.setContent {
            AppCompatTheme {
                QuantityItemListScreen(null)
            }
        }

        val contentDescription = composeTestRule.activity.getString(R.string.loading_indicator)
        composeTestRule.onNodeWithContentDescription(contentDescription).assertIsDisplayed()
    }

    @Test
    fun itemsIsNotNull_loadingIndicatorIsNotDisplayed() {
        val items = listOf(
            QuantityItemModel(1, "Item 1", "5"),
            QuantityItemModel(2, "Item 2", "6")
        )

        composeTestRule.setContent {
            AppCompatTheme {
                QuantityItemListScreen(items)
            }
        }

        val contentDescription = composeTestRule.activity.getString(R.string.loading_indicator)
        composeTestRule.onNodeWithContentDescription(contentDescription).assertDoesNotExist()
    }
}