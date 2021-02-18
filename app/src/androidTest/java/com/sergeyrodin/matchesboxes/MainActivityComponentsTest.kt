package com.sergeyrodin.matchesboxes

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.di.RadioComponentsDataSourceModule
import com.sergeyrodin.matchesboxes.di.TestModule
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TestModule::class)
class MainActivityComponentsTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataSource: RadioComponentsDataSource

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun initDataSource() {
        hiltRule.inject()
        runBlocking {
            dataSource.clearDatabase()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun selectComponent_detailsDisplayed() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 4, box.id, true)

        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(bag.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(set.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(box.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(component.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(component.quantity.toString()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.buy_checkbox))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        activityScenario.close()
    }

    @Test
    fun detailsFragment_editFabClick_namesDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 4, box.id, true)

        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(bag.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(set.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(box.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(component.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(component.quantity.toString()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.buy_checkbox))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        activityScenario.close()
    }

    @Test
    fun componentUpdated_boxNameEquals() = runBlocking {
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
        val component = RadioComponent(1, "Component", 4, box2.id)
        dataSource.insertBag(bag1)
        dataSource.insertBag(bag2)
        dataSource.insertMatchesBoxSet(set1)
        dataSource.insertMatchesBoxSet(set2)
        dataSource.insertMatchesBoxSet(set3)
        dataSource.insertMatchesBoxSet(set4)
        dataSource.insertMatchesBox(box1)
        dataSource.insertMatchesBox(box2)
        dataSource.insertMatchesBox(box3)
        dataSource.insertMatchesBox(box4)
        dataSource.insertMatchesBox(box5)
        dataSource.insertMatchesBox(box6)
        dataSource.insertMatchesBox(box7)
        dataSource.insertMatchesBox(box8)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box2.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(bag1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(bag2.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set3.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set4.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box7.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box8.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(box8.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun boxChanged_componentAdded_nameDisplayed() = runBlocking {
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
        dataSource.insertBag(bag1)
        dataSource.insertBag(bag2)
        dataSource.insertMatchesBoxSet(set1)
        dataSource.insertMatchesBoxSet(set2)
        dataSource.insertMatchesBoxSet(set3)
        dataSource.insertMatchesBoxSet(set4)
        dataSource.insertMatchesBox(box1)
        dataSource.insertMatchesBox(box2)
        dataSource.insertMatchesBox(box3)
        dataSource.insertMatchesBox(box4)
        dataSource.insertMatchesBox(box5)
        dataSource.insertMatchesBox(box6)
        dataSource.insertMatchesBox(box7)
        dataSource.insertMatchesBox(box8)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box2.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(bag1.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(bag2.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set3.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set4.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box7.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box8.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Component")).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(box8.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addComponent_nameEquals() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        // Add bag
        Espresso.onView(ViewMatchers.withId(R.id.add_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.bag_edit)).perform(ViewActions.typeText("Bag"))
        Espresso.onView(ViewMatchers.withId(R.id.save_bag_fab)).perform(ViewActions.click())

        // Add matches box set
        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit)).perform(ViewActions.typeText("Set"))
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        // Add matches box
        Espresso.onView(ViewMatchers.withText("Set")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit)).perform(ViewActions.typeText("Box"))
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        // Add component
        Espresso.onView(ViewMatchers.withText("Box")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.items))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Component"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addTwoComponents_namesEqual() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component2"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Component"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Component2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addComponent_deleteButtonVisible() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete))
            .check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @Test
    fun updateComponent_deleteButtonVisible() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addComponent_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.add_component))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addComponent_checkBuy_buyChecked() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.buy_checkbox)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Component")).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.buy_checkbox))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        activityScenario.close()
    }

    @Test
    fun editComponent_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.update_component))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun updateComponent_nameInListUpdated() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.replaceText("Component updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Component updated"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editComponent_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteQuantity_quantityZero() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.quantity_edit))
            .perform(ViewActions.replaceText(""))
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Component")).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("0"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }
}