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
    private lateinit var subject: AddEditDeleteMatchesBoxSetFragment

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
        val bagId = 1
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(ADD_NEW_ITEM_ID, bagId).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.enter_matches_box_set_name)).check(matches(isDisplayed()))
    }

    @Test
    fun idArg_nameEquals() {
        val set = MatchesBoxSet(1, "MBS1", 1)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(set.id, set.bagId).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.set_edit)).check(matches(withText(set.name)))
    }

    @Test
    fun addNewSet_nameNavigationEquals() = runBlocking{
        val bagId = 1
        dataSource.addMatchesBoxSets()
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(ADD_NEW_ITEM_ID, bagId).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.set_edit)).perform(typeText("MBS1"))
        onView(withId(R.id.save_set_fab)).perform(click())

        // Test name
        val item = dataSource.getMatchesBoxSetsByBagId(bagId)[0]
        Assert.assertThat(item.name, `is`("MBS1"))

        // Test navigation
        verify(navController).navigate(
            AddEditDeleteMatchesBoxSetFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(bagId)
        )
    }

    @Test
    fun updateSet_nameAndNavigationEquals() = runBlocking{
        val bagId = 2
        val set = MatchesBoxSet(1, "MBS1", bagId)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(set.id, bagId).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.set_edit)).perform(replaceText("MBS1 updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        val item = dataSource.getMatchesBoxSetById(set.id)
        Assert.assertThat(item?.name, `is`("MBS1 updated"))

        // TODO: add navigation test
    }

    @Test
    fun deleteSet_nameNameNavigationEquals() = runBlocking{
        val bagId = 2
        val set = MatchesBoxSet(1, "MBS", bagId)
        dataSource.addMatchesBoxSets(set)
        val bundle = AddEditDeleteMatchesBoxSetFragmentArgs.Builder(set.id, bagId).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxSetFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }
        clickDeleteAction(scenario)

        val items = dataSource.getMatchesBoxSetsByBagId(bagId)
        Assert.assertThat(items.size, `is`(0))

        verify(navController).navigate(
            AddEditDeleteMatchesBoxSetFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(bagId)
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