package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@MediumTest
@RunWith(AndroidJUnit4::class)
class BagManipulatorFragmentTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun resetDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun nulArg_enterNameTextEquals() {
        val bundle = BagManipulatorFragmentArgs.Builder(ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.enter_bag_name)).check(matches(isDisplayed()))
    }

    @Test
    fun bagId_textEquals() {
        val bag = Bag(1, "New bag")
        dataSource.addBags(bag)
        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.bag_edit)).check(matches(withText(bag.name)))
    }

    @Test
    fun nullArg_addNewBag() = runBlocking{
        dataSource.addBags()
        val bundle = BagManipulatorFragmentArgs.Builder(ADD_NEW_ITEM_ID, "Title").build().toBundle()
        val scenario = launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.bag_edit)).perform(replaceText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        val bag = dataSource.getBags()[0]
        Assert.assertThat(bag.name, `is`("New bag"))

        verify(navController).navigate(
            BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
        )
    }

    @Test
    fun bagIdArg_updateBagName() {
        val bag = Bag(1, "Old bag")
        val bagUpdated = Bag(1, "Bag updated")
        dataSource.addBags(bag.copy())
        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        val scenario = launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.bag_edit)).perform(replaceText(bagUpdated.name))
        onView(withId(R.id.save_bag_fab)).perform(click())

        verify(navController).navigate(
            BagManipulatorFragmentDirections
                .actionAddEditDeleteBagFragmentToMatchesBoxSetsListFragment(bagUpdated.id, bagUpdated.name)
        )
    }

    @Test
    fun nullArg_emptyInput_sizeZero() = runBlocking{
        dataSource.addBags()
        val bundle = BagManipulatorFragmentArgs.Builder(ADD_NEW_ITEM_ID, "Title").build().toBundle()
        launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.bag_edit)).perform(replaceText(" "))
        onView(withId(R.id.save_bag_fab)).perform(click())

        val bags = dataSource.getBags()
        Assert.assertThat(bags.size, `is`(0))
    }

    @Test
    fun bagIdArg_emptyInput_nameNotChanged() = runBlocking{
        val bag = Bag(1, "Old bag")
        dataSource.addBags(bag)
        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.bag_edit)).perform(replaceText(" "))
        onView(withId(R.id.save_bag_fab)).perform(click())

        val updatedBag = dataSource.getBags()[0]
        Assert.assertThat(updatedBag.name, `is`("Old bag"))
    }

    @Test
    fun deleteBagAndNavigate() = runBlocking{
        val bag = Bag(1, "Bag to delete")
        dataSource.addBags(bag)
        val bundle = BagManipulatorFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        val scenario = launchFragmentInContainer<BagManipulatorFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickDeleteAction(scenario)

        val bags = dataSource.getBags()
        Assert.assertThat(bags.size, `is`(0))
        verify(navController).navigate(
            BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
        )
    }

    private fun clickDeleteAction(scenario: FragmentScenario<BagManipulatorFragment>) {
        // Create dummy menu item with the desired item id
        val context = getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(deleteMenuItem)
        }
    }

}