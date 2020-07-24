package com.sergeyrodin.matchesboxes

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
import com.sergeyrodin.matchesboxes.util.DataBindingIdlingResource
import com.sergeyrodin.matchesboxes.util.EspressoIdlingResource
import com.sergeyrodin.matchesboxes.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
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
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withId(R.id.add_bag_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.bag_edit)).perform(ViewActions.typeText("New bag"))
        Espresso.onView(ViewMatchers.withId(R.id.save_bag_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.bag_added))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBag_toastShow() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.bag_edit))
            .perform(ViewActions.replaceText("Bag updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_bag_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.bag_updated))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteBag_toastShow() = runBlocking {
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.bag_deleted))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    // Set

    @Test
    fun addSet_showToast() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText("Bag")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_set_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit)).perform(ViewActions.typeText("MBS1"))
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.matches_box_set_added))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun updateSet_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.set_edit))
            .perform(ViewActions.replaceText("Set updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_set_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.matches_box_set_updated))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteSet_showToast() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.matches_box_set_deleted))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    // Box

    @Test
    fun addMatchesBox_toastShown() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.box_edit)).perform(ViewActions.typeText("New box"))
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.box_added))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.box_edit))
            .perform(ViewActions.replaceText("Updated box"))
        Espresso.onView(ViewMatchers.withId(R.id.save_box_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.box_updated))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.box_deleted))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component"))
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.component_added))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_component_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.save_component_error))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.component_edit))
            .perform(ViewActions.typeText("Component updated"))
        Espresso.onView(ViewMatchers.withId(R.id.save_component_fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.component_updated))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        Espresso.onView(ViewMatchers.withText(bag.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(set.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(box.name)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(component.name)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.action_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.component_deleted))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }
}