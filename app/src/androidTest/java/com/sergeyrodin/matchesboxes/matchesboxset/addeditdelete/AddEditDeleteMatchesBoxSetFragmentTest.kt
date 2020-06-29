package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
class AddEditDeleteMatchesBoxSetFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.radioComponentsDataSource = dataSource
    }

    @After
    fun clearDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun addNewSet_hintDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "MBS1", bag.id)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(bag, null).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.enter_matches_box_set_name)).check(matches(isDisplayed()))
    }

    @Test
    fun idArg_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(15, "MBS1", 2)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(bag, set).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.set_edit)).check(matches(withText(set.name)))
    }

    @Test
    fun addNewSet_navigationCalled() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(bag, null).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.set_edit)).perform(replaceText("New set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        // Test navigation
        verify(navController).navigate(
            AddEditDeleteMatchesBoxSetFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(bag)
        )
    }

    @Test
    fun updateSet_navigationCalled() = runBlocking{
        val bag = Bag(2, "Bag")
        val set = MatchesBoxSet(1, "MBS1", bag.id)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(bag, set).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.save_set_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteMatchesBoxSetFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxListFragment(bag, set)
        )
    }

    @Test
    fun deleteSet_sizeNavigationEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "MBS", bag.id)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(bag, set).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }
        clickDeleteAction(scenario)

        val items = dataSource.getMatchesBoxSetsByBagId(bag.id)
        Assert.assertThat(items.size, `is`(0))

        verify(navController).navigate(
            AddEditDeleteMatchesBoxSetFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(bag)
        )
    }

    private fun clickDeleteAction(
        scenario: FragmentScenario<AddEditDeleteMatchesBoxSetFragment>
    ) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(deleteMenuItem)
        }
    }
}