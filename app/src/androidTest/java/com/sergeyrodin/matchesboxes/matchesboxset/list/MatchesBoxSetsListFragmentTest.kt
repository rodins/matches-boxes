package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RadioComponentsDataSourceModule::class)
class MatchesBoxSetsListFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun addSets_namesDisplayed() {
        val bag = Bag(1, "Bag")
        dataSource.addMatchesBoxSets(
            MatchesBoxSet(1, "MBS1", bag.id),
            MatchesBoxSet(2, "MBS2", bag.id),
            MatchesBoxSet(3, "MBS3", bag.id)
        )

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withText("MBS1")).check(matches(isDisplayed()))
        onView(withText("MBS2")).check(matches(isDisplayed()))
        onView(withText("MBS3")).check(matches(isDisplayed()))
    }

    @Test
    fun noSets_textDisplayed() {
        val bag = Bag(1, "Bag")
        dataSource.addMatchesBoxSets()
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))
    }

    @Test
    fun noSets_addSetFabClicked_navigationCalled() {
        val bag = Bag(1, "Bag")

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.add_set)
        }

        onView(withId(R.id.add_set_fab)).perform(click())

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(bag.id, ADD_NEW_ITEM_ID, title)
        )
    }

    @Test
    fun selectItem_navigationCalled() {
        val bag = Bag(2, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.addMatchesBoxSets(set)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withText(set.name)).perform(click())

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToMatchesBoxListFragment(set.id, set.name)
        )
    }

    @Test
    fun clickEditAction_navigationMatches() {
        val bag = Bag(1, "Bag")

        val navController = Mockito.mock(NavController::class.java)
        var title = ""

        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(requireView(), navController)
            title = getString(R.string.update_bag)
            clickEditAction(this)
        }

        verify(navController).navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteBagFragment(bag.id, title)
        )
    }

    private fun clickEditAction(fragment: Fragment) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editMenuItem = ActionMenuItem(context, 0, R.id.action_edit, 0, 0, null)
        fragment.onOptionsItemSelected(editMenuItem)
    }

    @Test
    fun setsList_quantityDisplayed() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component1 = RadioComponent(1, "Component1", 1, box1.id)
        val component2 = RadioComponent(2, "Component2", 2, box1.id)
        val component3 = RadioComponent(3, "Component3", 3, box2.id)
        val component4 = RadioComponent(4, "Component4", 4, box2.id)
        val component5 = RadioComponent(5, "Component5", 5, box3.id)
        val component6 = RadioComponent(6, "Component6", 6, box3.id)
        val component7 = RadioComponent(7, "Component7", 7, box4.id)
        val component8 = RadioComponent(8, "Component8", 8, box4.id)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component1, component2, component3, component4,
            component5, component6, component7, component8)
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bag.id, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withText("10")).check(matches(isDisplayed()))
        onView(withText("26")).check(matches(isDisplayed()))
    }

    @Test
    fun setListIconIsDisplayed() {
        val bagId = 1
        val set = MatchesBoxSet(1, "Set", bagId)
        dataSource.addMatchesBoxSets(set)
        val bundle = MatchesBoxSetsListFragmentArgs.Builder(bagId, "Title").build().toBundle()
        launchFragmentInHiltContainer<MatchesBoxSetsListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.items)).check(matches(hasDescendant(withTagValue(equalTo(R.drawable.ic_set)))))
    }
}