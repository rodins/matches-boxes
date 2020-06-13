package com.sergeyrodin.matchesboxes

import android.view.View
import android.widget.AutoCompleteTextView
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

    // Bag tests

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
    fun editBagClick_editBag_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())

        onView(withId(R.id.bag_edit)).perform(replaceText("Bag updated"))
        onView(withId(R.id.save_bag_fab)).perform(click())
        onView(withText("Bag updated")).check(matches(isDisplayed()))
        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBagClick_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())

        onView(withId(R.id.bag_edit)).check(matches(withText(bag.name)))

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
    fun bagClick_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(bag.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBag_titleEquals() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withText(R.string.add_bag)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBag_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withText(R.string.update_bag)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBag_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.bag_edit)).check(matches(withText(bag.name)))

        activityScenario.close()
    }

    // MatchesBoxSet tests

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
    fun addTwoSets_namesEqual() = runBlocking {
        dataSource.insertBag(Bag(1, "Bag"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Bag")).perform(click())

        onView(withText(R.string.no_matches_box_sets_added)).check(matches(isDisplayed()))

        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("Set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withText("Set")).check(matches(isDisplayed()))

        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("Set2"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withText("Set2")).check(matches(isDisplayed()))

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
    fun updateSet_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).perform(replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())
        // TODO: display set name as title to matches boxes list
        //onView(withText("Set updated")).check(matches(isDisplayed()))
        onView(withText(R.string.no_matches_boxes_added)).check(matches(isDisplayed()))

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).perform(replaceText("Set updated"))
        onView(withId(R.id.save_set_fab)).perform(click())

        onView(withText(R.string.matches_box_set_updated))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.matches_box_set_deleted))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun setClick_titleEquals() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(set.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSet_titleEquals() = runBlocking {
        val bag = Bag(1, "Bag")
        dataSource.insertBag(bag)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withText(R.string.add_set)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editSet_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withText(R.string.update_set)).perform(click())

        activityScenario.close()
    }

    @Test
    fun editSet_nameEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.set_edit)).check(matches(withText(set.name)))

        activityScenario.close()
    }

    // MatchesBox tests

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
    fun addTwoBoxes_namesEqual() = runBlocking{
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

        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("Box2"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withText("Box2")).check(matches(isDisplayed()))

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.box_edit)).perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        onView(withText(R.string.box_updated))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun updateBox_nameEquals() = runBlocking{
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
        onView(withId(R.id.box_edit)).perform(replaceText("Updated box"))
        onView(withId(R.id.save_box_fab)).perform(click())
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withText("Updated box")).check(matches(isDisplayed()))

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.box_deleted))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun boxClicked_titleEquals() = runBlocking{
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
        onView(withText(box.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addBox_titleEquals() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withText(R.string.add_box)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBox_titleEquals() = runBlocking{
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
        onView(withText(R.string.update_box)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editBox_nameEquals() = runBlocking{
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
        onView(withId(R.id.box_edit)).check(matches(withText(box.name)))

        activityScenario.close()
    }

    // RadioComponent tests

    @Test
    fun addComponent_nameEquals() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add bag
        onView(withId(R.id.add_bag_fab)).perform(click())
        onView(withId(R.id.bag_edit)).perform(typeText("Bag"))
        onView(withId(R.id.save_bag_fab)).perform(click())

        // Add matches box set
        onView(withText("Bag")).perform(click())
        onView(withId(R.id.add_set_fab)).perform(click())
        onView(withId(R.id.set_edit)).perform(typeText("Set"))
        onView(withId(R.id.save_set_fab)).perform(click())

        // Add matches box
        onView(withText("Set")).perform(click())
        onView(withId(R.id.add_box_fab)).perform(click())
        onView(withId(R.id.box_edit)).perform(typeText("Box"))
        onView(withId(R.id.save_box_fab)).perform(click())

        // Add component
        onView(withText("Box")).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("Component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withId(R.id.items)).check(matches(isDisplayed()))
        onView(withText("Component")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addTwoComponents_namesEqual() = runBlocking{
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
        onView(withId(R.id.component_edit)).perform(typeText("Component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("Component2"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withText("Component")).check(matches(isDisplayed()))
        onView(withText("Component2")).check(matches(isDisplayed()))

        activityScenario.close()
    }

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.component_edit)).perform(typeText("Component"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withText(R.string.component_added))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())

        onView(withId(R.id.component_edit)).perform(typeText("Component updated"))
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withText(R.string.component_updated))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

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

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())

        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.component_deleted))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withId(R.id.action_delete)).check(doesNotExist())

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))

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
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withId(R.id.add_component_fab)).perform(click())
        onView(withText(R.string.add_component)).check(matches(isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withText(R.string.update_component)).check(matches(isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText(bag.name)).perform(click())
        onView(withText(set.name)).perform(click())
        onView(withText(box.name)).perform(click())
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.component_edit)).check(matches(withText(component.name)))

        activityScenario.close()
    }

    // Search
    @Test
    fun searchQuery_nameMatches() = runBlocking{
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

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("comp\n"))//.perform(pressKey(KeyEvent.KEYCODE_ENTER))
        onView(withText(component.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

}