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
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.ServiceLocator
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.FakeDataSource
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.matchesbox.addeditdelete.AddEditDeleteMatchesBoxFragment
import com.sergeyrodin.matchesboxes.matchesbox.addeditdelete.AddEditDeleteMatchesBoxFragmentArgs
import com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete.AddEditDeleteMatchesBoxSetFragment
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.Assert.*
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
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, ADD_NEW_ITEM_ID, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.component_edit)).check(matches(withHint(R.string.enter_component_name)))
    }

    @Test
    fun noItem_isBuyNotChecked() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, ADD_NEW_ITEM_ID, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buy_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun argItem_nameEquals() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.component_edit)).check(matches(withText(component.name)))
    }

    @Test
    fun argItem_buyFalse_buyNotChecked() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buy_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun argItem_buyTrue_buyChecked() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId, true)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buy_checkbox)).check(matches(isChecked()))
    }

    @Test
    fun addItem_navigationCalled() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        dataSource.addRadioComponents()
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, ADD_NEW_ITEM_ID, setId, bag).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.component_edit)).perform(replaceText("New component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteRadioComponentFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(boxId, setId, bag)
        )
    }

    @Test
    fun addItem_quantityHintZero() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        dataSource.addRadioComponents()
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, ADD_NEW_ITEM_ID, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withHint(R.string.quantity_hint)).check(matches(isDisplayed()))
    }

    @Test
    fun updateItem_navigationCalled() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.component_edit)).perform(replaceText("Update component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        verify(navController).navigate(
            AddEditDeleteRadioComponentFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(boxId, setId, bag)
        )
    }

    @Test
    fun deleteItem_navigationCalled() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        val scenario = launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        clickDeleteAction(scenario)

        verify(navController).navigate(
            AddEditDeleteRadioComponentFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(boxId, setId, bag)
        )
    }

    @Test
    fun plusButtonClick_quantityEquals() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.quantity_edit)).check(matches(withText("3")))

        onView(withId(R.id.buttonPlus)).perform(click())

        onView(withId(R.id.quantity_edit)).check(matches(withText("4")))
    }

    @Test
    fun minusButtonClick_quantityEquals() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 3, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.quantity_edit)).check(matches(withText("3")))

        onView(withId(R.id.buttonMinus)).perform(click())

        onView(withId(R.id.quantity_edit)).check(matches(withText("2")))
    }

    @Test
    fun quantityZero_minusDisabled() {
        val boxId = 1
        val setId = 1
        val bag = Bag(1, "Bag")
        val component = RadioComponent(1, "Component", 1, boxId)
        dataSource.addRadioComponents(component)
        val bundle = AddEditDeleteRadioComponentFragmentArgs.Builder(boxId, component.id, setId, bag).build().toBundle()
        launchFragmentInContainer<AddEditDeleteRadioComponentFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.buttonMinus)).check(matches(isEnabled()))

        onView(withId(R.id.buttonMinus)).perform(click())

        onView(withId(R.id.buttonMinus)).check(matches(not(isEnabled())))
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