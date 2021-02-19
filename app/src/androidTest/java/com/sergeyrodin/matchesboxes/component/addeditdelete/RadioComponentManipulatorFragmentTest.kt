package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
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
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.not
import org.junit.After
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
class RadioComponentManipulatorFragmentTest {

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
    fun noItem_hintDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        launchFragmentWithArgs(box)

        onView(withId(R.id.component_edit)).check(matches(withHint(R.string.enter_component_name)))
    }

    @Test
    fun noItem_isBuyNotChecked() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        launchFragmentWithArgs(box)

        onView(withId(R.id.buy_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun argItem_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withId(R.id.component_edit)).check(matches(withText(component.name)))
    }

    @Test
    fun argItem_buyFalse_buyNotChecked() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withId(R.id.buy_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun argItem_buyTrue_buyChecked() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id, true)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withId(R.id.buy_checkbox)).check(matches(isChecked()))
    }

    @Test
    fun addItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents()

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundle(box)
        launchFragmentWithNavController(bundle, navController)

        onView(withId(R.id.component_edit)).perform(replaceText("New component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    box.id,
                    box.name
                )
        )
    }

    @Test
    fun addItem_quantityHintZero() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()
        launchFragmentWithArgs(box)

        onView(withHint(R.string.quantity_hint)).check(matches(isDisplayed()))
    }

    @Test
    fun updateItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundleComponentsList(component, box)
        launchFragmentWithNavController(bundle, navController)

        onView(withId(R.id.component_edit)).perform(replaceText("Update component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    box.id,
                    box.name
                )
        )
    }

    @Test
    fun deleteItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundleComponentsList(component, box)
        launchFragmentInHiltContainer<RadioComponentManipulatorFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(view!!, navController)
            clickDeleteAction(this)
        }

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    box.id,
                    box.name
                )
        )
    }

    @Test
    fun plusButtonClick_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withId(R.id.quantity_edit)).check(matches(withText("3")))

        onView(withId(R.id.buttonPlus)).perform(click())

        onView(withId(R.id.quantity_edit)).check(matches(withText("4")))
    }

    @Test
    fun minusButtonClick_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withId(R.id.quantity_edit)).check(matches(withText("3")))

        onView(withId(R.id.buttonMinus)).perform(click())

        onView(withId(R.id.quantity_edit)).check(matches(withText("2")))
    }

    @Test
    fun quantityZero_minusDisabled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 1, box.id)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withId(R.id.buttonMinus)).check(matches(isEnabled()))

        onView(withId(R.id.buttonMinus)).perform(click())

        onView(withId(R.id.buttonMinus)).check(matches(not(isEnabled())))
    }

    // Spinners

    @Test
    fun boxId_boxNameDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box1 = MatchesBox(1, "Box1", set.id)
        val box2 = MatchesBox(2, "Box2", set.id)
        val box3 = MatchesBox(3, "Box3", set.id)
        val box4 = MatchesBox(4, "Box4", set.id)
        val component = RadioComponent(1, "Component", 3, box3.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component)

        launchFragmentWithArgs(component, box3)

        onView(withText(box3.name)).check(matches(isDisplayed()))
    }

    @Test
    fun boxId_setNameDisplayed() {
        val bag = Bag(1, "Bag")
        val set1 = MatchesBoxSet(1, "Set1", bag.id)
        val set2 = MatchesBoxSet(2, "Set2", bag.id)
        val set3 = MatchesBoxSet(3, "Set3", bag.id)
        val set4 = MatchesBoxSet(4, "Set4", bag.id)
        val box = MatchesBox(1, "Box", set3.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withText(set3.name)).check(matches(isDisplayed()))
    }

    @Test
    fun boxId_bagNameDisplayed() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val bag3 = Bag(3, "Bag3")
        val bag4 = Bag(4, "Bag4")
        val set = MatchesBoxSet(1, "Set", bag3.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addBags(bag1, bag2, bag3, bag4)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box)

        onView(withText(bag3.name)).check(matches(isDisplayed()))
    }

    @Test
    fun setSelect_boxChanged() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        val component = RadioComponent(1, "Component", 4, box5.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box5)

        onView(withText(set3.name)).perform(click())
        onView(withText(set4.name)).perform(click())

        onView(withText(box7.name)).check(matches(isDisplayed()))
    }

    @Test
    fun bagSelect_setsAndBoxesChanged() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        val component = RadioComponent(1, "Component", 4, box5.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box5)

        onView(withText(bag2.name)).perform(click())
        onView(withText(bag1.name)).perform(click())

        onView(withText(set1.name)).check(matches(isDisplayed()))
        onView(withText(box1.name)).check(matches(isDisplayed()))
    }

    @Test
    fun boxChanged_componentUpdated_navigationCalled() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        val component = RadioComponent(1, "Component", 4, box5.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundleComponentsList(component, box5)
        launchFragmentWithNavController(bundle, navController)

        onView(withText(box5.name)).perform(click())
        onView(withText(box6.name)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    box6.id,
                    box6.name
                )
        )
    }

    @Test
    fun boxChanged_componentAdded_navigationCalled() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundle(box5)
        launchFragmentWithNavController(bundle, navController)

        onView(withText(box5.name)).perform(click())
        onView(withText(box6.name)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("Component"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    box6.id,
                    box6.name
                )
        )
    }

    private fun launchFragmentWithArgs(box: MatchesBox) {
        val bundle = createBundle(box)
        launchFragment(bundle)
    }

    private fun createBundle(box: MatchesBox): Bundle {
        return RadioComponentManipulatorFragmentArgs.Builder(
            ADD_NEW_ITEM_ID,
            box.id,
            "Title",
            "",
            RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
        ).build().toBundle()
    }

    @Test
    fun bagWithNoSetsSelected_noSetsTextDisplayed() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component = RadioComponent(1, "Component", 3, box1.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box1)

        onView(withText(bag1.name)).perform(click())
        onView(withText(bag2.name)).perform(click())

        onView(withText(R.string.no_sets)).check(matches(isDisplayed()))
    }

    @Test
    fun bagWithNoSetsSelected_noBoxesTextDisplayed() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val component = RadioComponent(1, "Component", 3, box1.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2, box3, box4)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box1)

        onView(withText(bag1.name)).perform(click())
        onView(withText(bag2.name)).perform(click())

        onView(withText(R.string.no_boxes)).check(matches(isDisplayed()))
    }

    @Test
    fun setWithNoBoxesSelected_noBoxesTextDisplayed() {
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val component = RadioComponent(1, "Component", 3, box1.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2)
        dataSource.addMatchesBoxes(box1, box2)
        dataSource.addRadioComponents(component)
        launchFragmentWithArgs(component, box1)

        onView(withText(set1.name)).perform(click())
        onView(withText(set2.name)).perform(click())

        onView(withText(R.string.no_boxes)).check(matches(isDisplayed()))
    }

    private fun launchFragmentWithArgs(
        component: RadioComponent,
        box: MatchesBox
    ) {
        val bundle = createBundleComponentsList(component, box)
        launchFragment(bundle)
    }

    private fun createBundleComponentsList(
        component: RadioComponent,
        box: MatchesBox
    ): Bundle {
        return RadioComponentManipulatorFragmentArgs.Builder(
            component.id,
            box.id,
            "Title",
            "",
            RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
        ).build().toBundle()
    }

    @Test
    fun updateComponent_searchMode_navigationCalled() {
        val query = "78041"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 2, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundle(component, box, query)
        launchFragmentWithNavController(bundle, navController)

        onView(withText(R.string.button_plus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToSearchFragment().setQuery(query)
        )
    }

    @Test
    fun updateComponent_buyMode_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 2, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundleNeededComponents(component, box)
        launchFragmentWithNavController(bundle, navController)

        onView(withText(R.string.button_plus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections.actionAddEditDeleteRadioComponentFragmentToNeededComponentsFragment()
        )
    }

    private fun createBundleNeededComponents(
        component: RadioComponent,
        box: MatchesBox
    ): Bundle {
        return RadioComponentManipulatorFragmentArgs.Builder(
            component.id,
            box.id,
            "Title",
            "",
            RadioComponentManipulatorReturns.TO_NEEDED_LIST
        ).build().toBundle()
    }

    @Test
    fun searchMode_deleteComponent_navigationCalled() {
        val query = "7804"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 2, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundle(component, box, query)
        launchFragmentInHiltContainer<RadioComponentManipulatorFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(view!!, navController)
            clickDeleteAction(this)
        }

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToSearchFragment().setQuery(query)
        )
    }

    private fun createBundle(
        component: RadioComponent,
        box: MatchesBox,
        query: String
    ): Bundle {
        return RadioComponentManipulatorFragmentArgs.Builder(
            component.id,
            box.id,
            "Title",
            query,
            RadioComponentManipulatorReturns.TO_SEARCH_LIST
        ).build().toBundle()
    }

    @Test
    fun searchMode_addComponent_navigationCalled() {
        val query = "78041"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 2, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundle(query)
        launchFragmentWithNavController(bundle, navController)

        onView(withId(R.id.component_edit)).perform(typeText("STRW6753"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToSearchFragment()
                .setQuery(query)
        )
    }

    private fun createBundle(query: String): Bundle {
        return RadioComponentManipulatorFragmentArgs.Builder(
            ADD_NEW_ITEM_ID,
            NO_ID_SET,
            "Title",
            query,
            RadioComponentManipulatorReturns.TO_SEARCH_LIST
        ).build().toBundle()
    }

    private fun launchFragmentWithArgs(
        query: String
    ) {
        val bundle = createBundle(query)
        launchFragment(bundle)
    }

    @Test
    fun buyMode_addComponent_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 2, box.id)
        dataSource.addBags(bag)
        dataSource.addMatchesBoxSets(set)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents(component)

        val navController = Mockito.mock(NavController::class.java)

        val bundle = createBundleNewItemNeededComponents()
        launchFragmentWithNavController(bundle, navController)

        onView(withId(R.id.component_edit)).perform(typeText("STRW6753"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            RadioComponentManipulatorFragmentDirections.actionAddEditDeleteRadioComponentFragmentToNeededComponentsFragment()
        )
    }

    private fun launchFragmentWithNavController(
        bundle: Bundle,
        navController: NavController?
    ) {
        launchFragmentInHiltContainer<RadioComponentManipulatorFragment>(bundle, R.style.AppTheme) {
            Navigation.setViewNavController(view!!, navController)
        }
    }

    private fun createBundleNewItemNeededComponents(): Bundle {
        return RadioComponentManipulatorFragmentArgs.Builder(
            ADD_NEW_ITEM_ID,
            NO_ID_SET,
            "Title",
            "",
            RadioComponentManipulatorReturns.TO_NEEDED_LIST
        ).build().toBundle()
    }

    @Test
    fun searchMode_noBoxId_spinnersNamesMatch() {
        val query = "78"
        val bag1 = Bag(1, "Bag1")
        val bag2 = Bag(2, "Bag2")
        val set1 = MatchesBoxSet(1, "Set1", bag1.id)
        val set2 = MatchesBoxSet(2, "Set2", bag1.id)
        val set3 = MatchesBoxSet(3, "Set3", bag2.id)
        val set4 = MatchesBoxSet(4, "Set4", bag2.id)
        val box1 = MatchesBox(1, "Box1", set1.id)
        val box2 = MatchesBox(2, "Box2", set1.id)
        val box3 = MatchesBox(3, "Box3", set2.id)
        val box4 = MatchesBox(4, "Box4", set2.id)
        val box5 = MatchesBox(5, "Box5", set3.id)
        val box6 = MatchesBox(6, "Box6", set3.id)
        val box7 = MatchesBox(7, "Box7", set4.id)
        val box8 = MatchesBox(8, "Box8", set4.id)
        dataSource.addBags(bag1, bag2)
        dataSource.addMatchesBoxSets(set1, set2, set3, set4)
        dataSource.addMatchesBoxes(box1, box2, box3, box4, box5, box6, box7, box8)
        launchFragmentWithArgs(query)

        onView(withText(bag1.name)).check(matches(isDisplayed()))
        onView(withText(set1.name)).check(matches(isDisplayed()))
        onView(withText(box1.name)).check(matches(isDisplayed()))
    }

    @Test
    fun searchMode_noBags_noBagsTextDisplayed() {
        val query = "78"
        dataSource.addBags()
        dataSource.addMatchesBoxSets()
        dataSource.addMatchesBoxes()
        val bundle = createBundle(query)
        launchFragment(bundle)

        onView(withText(R.string.no_bags)).check(matches(isDisplayed()))
    }

    private fun launchFragment(bundle: Bundle) {
        launchFragmentInHiltContainer<RadioComponentManipulatorFragment>(bundle, R.style.AppTheme)
    }

    private fun clickDeleteAction(
        fragment: Fragment
    ) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        fragment.onOptionsItemSelected(deleteMenuItem)
    }

}