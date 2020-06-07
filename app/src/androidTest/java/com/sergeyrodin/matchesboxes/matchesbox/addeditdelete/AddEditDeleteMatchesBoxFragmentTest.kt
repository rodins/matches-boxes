package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

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
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete.AddEditDeleteMatchesBoxSetFragment
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
class AddEditDeleteMatchesBoxFragmentTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun addItem_hintDisplayed() {
        val setId = 1
        val boxId = ADD_NEW_ITEM_ID
        dataSource.addMatchesBoxes(MatchesBox(1, "Box", setId))
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(setId, boxId).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.enter_box_name)).check(matches(isDisplayed()))
    }

    @Test
    fun itemIdArg_nameEquals() {
        val setId = 2
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(setId, box.id).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.box_edit)).check(matches(withText(box.name)))
    }

    @Test
    fun addItem_saveName_nameEquals() = runBlocking{
        val setId = 2
        dataSource.addMatchesBoxes()
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(setId, ADD_NEW_ITEM_ID).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.box_edit)).perform(replaceText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        val item = dataSource.getMatchesBoxById(1)
        Assert.assertThat(item?.name, `is`("New box"))

        verify(navController).navigate(
            AddEditDeleteMatchesBoxFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(setId)
        )
    }

    @Test
    fun updateItem_saveName_nameEquals() = runBlocking{
        val setId = 2
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(setId, box.id).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.box_edit)).perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        val item = dataSource.getMatchesBoxById(box.id)
        Assert.assertThat(item?.name, `is`("Updated box"))
    }

    @Test
    fun updateBox_navigationCalled() = runBlocking{
        val setId = 2
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(setId, box.id).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.box_edit)).perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteMatchesBoxFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToRadioComponentsListFragment(box.id)
        )
    }

    @Test
    fun deleteItem_menuItemClick_sizeZero() = runBlocking{
        val setId = 2
        val box = MatchesBox(1, "Box", setId)
        dataSource.addMatchesBoxes(box)
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(setId, box.id).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickDeleteAction(scenario)

        val items = dataSource.getMatchesBoxesByMatchesBoxSetId(setId)
        Assert.assertThat(items.size, `is`(0))

        verify(navController).navigate(
            AddEditDeleteMatchesBoxFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(setId)
        )
    }

    private fun clickDeleteAction(
        scenario: FragmentScenario<AddEditDeleteMatchesBoxFragment>
    ) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(deleteMenuItem)
        }
    }
}