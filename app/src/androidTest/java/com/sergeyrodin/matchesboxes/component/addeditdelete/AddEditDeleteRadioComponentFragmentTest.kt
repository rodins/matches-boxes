package com.sergeyrodin.matchesboxes.component.addeditdelete

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
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.*
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class AddEditDeleteRadioComponentFragmentTest {
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
    fun noItem_hintDisplayed() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(ADD_NEW_ITEM_ID, box.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.component_edit)).check(matches(withHint(R.string.enter_component_name)))
    }

    @Test
    fun noItem_isBuyNotChecked() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(ADD_NEW_ITEM_ID, box.id,"Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buy_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun argItem_nameEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id,"Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.component_edit)).check(matches(withText(component.name)))
    }

    @Test
    fun argItem_buyFalse_buyNotChecked() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id,"Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buy_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun argItem_buyTrue_buyChecked() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id, true)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id,"Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buy_checkbox)).check(matches(isChecked()))
    }

    @Test
    fun addItem_navigationCalled() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addMatchesBoxes(box)
        dataSource.addRadioComponents()
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(ADD_NEW_ITEM_ID, box.id,"Title").build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.component_edit)).perform(replaceText("New component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteRadioComponentFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(box.id, box.name)
        )
    }

    @Test
    fun addItem_quantityHintZero() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.addRadioComponents()
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(ADD_NEW_ITEM_ID, box.id,"Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

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
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.component_edit)).perform(replaceText("Update component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteRadioComponentFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(box.id, box.name)
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
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickDeleteAction(scenario)

        verify(navController).navigate(
            AddEditDeleteRadioComponentFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(box.id, box.name)
        )
    }

    @Test
    fun plusButtonClick_quantityEquals() {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

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
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

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
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

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

        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box3.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

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
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

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
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(component.id, box.id, "Title").build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withText(bag3.name)).check(matches(isDisplayed()))
    }


    private fun clickDeleteAction(
        scenario: FragmentScenario<AddEditDeleteRadioComponentFragment>
    ) {
        // Create dummy menu item with the desired item id
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deleteMenuItem = ActionMenuItem(context, 0, R.id.action_delete, 0, 0, null)
        scenario.onFragment{
            it.onOptionsItemSelected(deleteMenuItem)
        }
    }

}