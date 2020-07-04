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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
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
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxes(MatchesBox(1, "Box", set.id))
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(set.id, null).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.enter_box_name)).check(matches(isDisplayed()))
    }

    @Test
    fun itemIdArg_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(set.id, box).build().toBundle()
        launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.box_edit)).check(matches(withText(box.name)))
    }

    @Test
    fun addItem_saveName_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxes()
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(set.id, null).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.box_edit)).perform(replaceText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteMatchesBoxFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(set.id)
        )
    }

    @Test
    fun updateBox_navigationCalled() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val updatedBox = MatchesBox(1, "Updated box", set.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box.copy())
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(set.id, box).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.box_edit)).perform(replaceText(updatedBox.name))
        onView(withId(R.id.save_box_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteMatchesBoxFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToRadioComponentsListFragment(updatedBox)
        )
    }

    @Test
    fun deleteItem_menuItemClick_sizeZero() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        val bundle = AddEditDeleteMatchesBoxFragmentArgs.Builder(set.id, box).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteMatchesBoxFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickDeleteAction(scenario)

        val items = dataSource.getMatchesBoxesByMatchesBoxSetId(set.id)
        Assert.assertThat(items.size, `is`(0))

        verify(navController).navigate(
            AddEditDeleteMatchesBoxFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(set.id)
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