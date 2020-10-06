package com.sergeyrodin.matchesboxes

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ToastsTest {
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
    fun addBag_showToast() {
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(ViewActions.typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.bag_added)

        activityScenario.close()
    }

    private fun launchAndMonitorMainActivity(): ActivityScenario<MainActivity> {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        return activityScenario
    }

    private fun getDecorView(activityScenario: ActivityScenario<MainActivity>): View? {
        var decorView: View? = null
        activityScenario.onActivity {
            decorView = it.window.decorView
        }
        return decorView
    }

    private fun checkToastIsDisplayed(decorView: View?, stringId: Int) {
        onView(withText(stringId))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun editBag_toastShow() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit))
            .perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.bag_updated)

        activityScenario.close()
    }

    @Test
    fun deleteBag_toastShow() = runBlocking {
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(decorView, R.string.bag_deleted)

        activityScenario.close()
    }

    // Set

    @Test
    fun addSet_showToast() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(ViewActions.typeText("MBS1"))
        onView(withId(R.id.save_set_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.matches_box_set_added)

        activityScenario.close()
    }

    @Test
    fun updateSet_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit))
            .perform(ViewActions.replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.matches_box_set_updated)

        activityScenario.close()
    }

    @Test
    fun deleteSet_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(decorView, R.string.matches_box_set_deleted)

        activityScenario.close()
    }

    // Box

    @Test
    fun addMatchesBox_toastShown() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())

        onView(withId(R.id.box_edit)).perform(ViewActions.typeText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.box_added)

        activityScenario.close()
    }

    @Test
    fun updateBox_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit))
            .perform(ViewActions.replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.box_updated)

        activityScenario.close()
    }

    @Test
    fun deleteBox_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(decorView, R.string.box_deleted)

        activityScenario.close()
    }

    // Component

    @Test
    fun addComponent_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)

        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.component_added)

        activityScenario.close()
    }

    @Test
    fun addComponent_emptyName_showErrorToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)

        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.save_component_error)

        activityScenario.close()
    }

    @Test
    fun updateComponent_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())

        onView(withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component updated"))
        onView(withId(R.id.save_component_fab)).perform(click())

        checkToastIsDisplayed(decorView, R.string.component_updated)

        activityScenario.close()
    }

    @Test
    fun deleteComponent_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "Component", 3, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)

        val activityScenario = launchAndMonitorMainActivity()
        val decorView = getDecorView(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())

        onView(withId(R.id.action_delete)).perform(click())

        checkToastIsDisplayed(decorView, R.string.component_deleted)

        activityScenario.close()
    }
}