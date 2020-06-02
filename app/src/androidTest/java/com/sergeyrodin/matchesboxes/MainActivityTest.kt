package com.sergeyrodin.matchesboxes

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponentsDataSource
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
class MainActivityTest {
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
        dataSource = ServiceLocator.provideRadioComponentsDataSource(getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun addBag_bagNameDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())
        onView(withText("New bag")).check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun addBag_deleteButtonIsNotDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())

        onView(withId(R.id.action_delete)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun noBags_addBagClick_textEquals() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.no_bags_added_text)).check(matches(isDisplayed()))
        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withHint(R.string.enter_bag_name)))

        activityScenario.close()
    }

    @Test
    fun addBag_showToast() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("New bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        onView(withText(R.string.bag_added))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addMatchesBoxSet_nameMatches() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("MBS1"))
        onView(withId(R.id.save_set_fab)).perform(click())
        onView(withText("MBS1")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_showToast() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        var decorView: View? = null
        activityScenario.onActivity{
            decorView = it.window.decorView
        }

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("MBS1"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withText(R.string.matches_box_set_added))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_deleteButtonNotDisplayed() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.action_delete)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun editBagClick_editBag_nameEquals() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withText("Bag")))

        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())
        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withText("Bag updated")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBagClick_deleteBag() = runBlocking{
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())
        onView(withText(R.string.no_bags_added)).check(matches(isDisplayed()))

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

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        onView(withText(R.string.bag_updated))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

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

        onView(withText("Bag")).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.bag_deleted))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addMatchesBox_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("Box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withText("Box")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addMatchesBox_hideDeleteButton() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())

        onView(withId(R.id.action_delete)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun addMatchesBox_tostShown() = runBlocking{
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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())

        onView(withId(R.id.box_edit)).perform(typeText("New box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withText(R.string.box_added))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    //TODO: toast test matches box deleted
    //TODO: toast test matches box updated

}