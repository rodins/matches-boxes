package com.sergeyrodin.matchesboxes

import android.widget.AutoCompleteTextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {
    private lateinit var dataSource: RadioComponentsDataSource
    private val dataBindingIdlingResource = DataBindingIdlingResource()

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

    @Before
    fun init() {
        dataSource = ServiceLocator.provideRadioComponentsDataSource(ApplicationProvider.getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetDataSource()
    }

    // Bag

    @Test
    fun addBag_navigateBack() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))
        pressBack()
        onView(withId(R.id.no_bags_added_text)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBag_navigateUp() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.no_bags_added_text)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBagSaved_navigationBack() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }

        activityScenario.close()
    }

    @Test
    fun editBagSaved_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).perform(ViewActions.replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBagSaved_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).perform(ViewActions.replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteBag_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform((click()))

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }

        activityScenario.close()
    }

    @Test
    fun mainScreen_navigateBack() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        try {
            pressBack()
            fail()
        } catch (exc: Exception) {
            // test successful
        }

        activityScenario.close()
    }

    // Set

    @Test
    fun addSet_navigateBack() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())

        pressBack()

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_navigateUp() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSetSaved_navigateBack() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("New set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSetSaved_navigateUp() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("New set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editSetSaved_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).perform(ViewActions.replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editSetSaved_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).perform(ViewActions.replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun setDeleted_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun setDeleted_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    // Box
    @Test
    fun addBoxSaved_navigateBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBoxSaved_navigateUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBoxSaved_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(ViewActions.replaceText("Box updated"))
        onView(withId(R.id.save_box_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBoxSaved_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(ViewActions.replaceText("Box updated"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteBox_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteBox_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    // Component

    @Test
    fun addComponentSaved_navigateBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("New component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addComponentSaved_navigateUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("New component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editComponentSaved_navigateBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.component_edit)).perform(ViewActions.replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editComponentSaved_navigateBackAllLevels() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.component_edit)).perform(ViewActions.replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()
        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.add_set_fab)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.add_bag_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editComponentSaved_navigatUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.component_edit)).perform(ViewActions.replaceText("Component update"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteComponent_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        pressBack()

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteComponent_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 2, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withId(R.id.add_box_fab)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    // Search

    @Test
    fun searchEdit_navigationBack() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertRadioComponent(component3)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.quantity_text)).perform(ViewActions.replaceText("3"))
        onView(withId(R.id.save_component_fab)).perform(click())

        pressBack()

        onView(withText(bag.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun searchEdit_navigationUp() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "BUH1015HI", 3, box.id)
        val component2 = RadioComponent(2, "D2499", 3, box.id)
        val component3 = RadioComponent(3, "LA78041", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        dataSource.insertRadioComponent(component3)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("78041\n"))
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.quantity_text)).perform(ViewActions.replaceText("3"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        onView(withText(bag.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

}
