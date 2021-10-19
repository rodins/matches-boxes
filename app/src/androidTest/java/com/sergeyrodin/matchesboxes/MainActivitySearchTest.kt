package com.sergeyrodin.matchesboxes

import android.app.Activity
import android.widget.AutoCompleteTextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.matchesboxes.data.*
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

annotation class SearchTest

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TestModule::class)
class MainActivitySearchTest {

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

    @SearchTest
    @Test
    fun searchFragmentBottomNavigation_isDisplayed() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.searchFragment)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun navigateToSearchFragment_titleDisplayed() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        onView(withText(R.string.components)).check(matches(isEnabled()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchQuery_nameMatches() = runBlocking{
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
        val query = "78041"

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery(query)
        onView(withText(component3.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @SearchTest
    @Test
    fun searchQueryFromBagFragment_nameMatches() = runBlocking{
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
        val query = "78041"

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        onView(withId(R.id.action_search)).perform(click())
        typeQuery(query)
        onView(withText(component3.name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @SearchTest
    @Test
    fun searchQuery_selectComponent_nameEquals() = runBlocking{
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

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.component_name)).check(matches(withText(component3.name)))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchQuery_quantityPlus_quantityEquals() = runBlocking{
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

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withText(R.string.button_plus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())
        onView(withText(component3.name)).perform(click())
        onView(withText("4")).check(matches(isDisplayed()))
        onView(withId(R.id.quantity_text)).check(matches(withText("4")))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchQuery_typeQuantity_quantityEquals() = runBlocking{
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

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.quantity_edit)).perform(replaceText("6"))
        onView(withId(R.id.save_component_fab)).perform(click())
        onView(withText(component3.name)).perform(click())
        onView(withText("6")).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchQuery_typeSameQuantity_quantityEquals() = runBlocking{
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

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.quantity_edit)).perform(replaceText("3"))
        onView(withId(R.id.save_component_fab)).perform(click())
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.quantity_edit)).check(matches(withText("3")))

        activityScenario.close()
    }

    @SearchTest
    @Test
    fun searchComponent_titleEquals() = runBlocking{
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

        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component3.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withText(R.string.update_component)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchComponent_changeQuantity_navigateToSearchComponents() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withText(R.string.button_plus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withText(R.string.components)).check(matches(isEnabled()))

        activityScenario.close()
    }

    @SearchTest
    @Test
    fun searchMode_deleteComponent_navigateToSearchComponents() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")
        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.components)).check(matches(isEnabled()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun navigateToSearchFragment_actionSearchIsDisplayed() = runBlocking{
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")

        onView(withText(component.name)).check(matches(isDisplayed()))
        onView(withId(R.id.app_bar_search)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchInSearchFragment_nameDisplayed() = runBlocking {
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component1 = RadioComponent(1, "LA78041", 1, box.id)
        val component2 = RadioComponent(2, "BUH1015", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component1)
        dataSource.insertRadioComponent(component2)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery("78041")

        onView(withText(component1.name)).check(matches(isDisplayed()))

        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(clearText(), typeText("15\n"))

        onView(withText(component2.name))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun startSearchFragment_queryDisplayed() = runBlocking{
        val query = "78041"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery(query)

        onView(withText(query)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun navigateBackToSearchFragment_queryDisplayed() = runBlocking{
        val query = "78041"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        typeQuery(query)

        onView(withText(component.name)).perform(click())
        onView(withId(R.id.edit_component_fab)).perform(click())
        onView(withText(R.string.button_plus)).perform(click())
        onView(withId(R.id.save_component_fab)).perform(click())

        onView(withText(query)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchFragmentRotation_queryIsDisplayed() = runBlocking{
        val query = "78041"
        val bag = Bag(1, "Bag")
        val set = MatchesBoxSet(1, "Set", bag.id)
        val box = MatchesBox(1, "Box", set.id)
        val component = RadioComponent(1, "LA78041", 1, box.id)
        dataSource.insertBag(bag)
        dataSource.insertMatchesBoxSet(set)
        dataSource.insertMatchesBox(box)
        dataSource.insertRadioComponent(component)
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }

        moveToSearch()
        typeQuery(query)

        rotateDeviceToLandscape(activity, activityScenario, dataBindingIdlingResource)

        onView(withText(query))
            .check(matches(isDisplayed()))

        rotateDeviceToPortrait(activity, activityScenario, dataBindingIdlingResource)

        onView(withText(query)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    @SearchTest
    @Test
    fun searchMode_addComponent_nameEquals() = runBlocking{
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
        val component = RadioComponent(1, "LA78041", 3, box1.id)
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

        moveToSearch()
        typeQuery("78")
        onView(withId(R.id.add_search_buy_component_fab))
            .perform(click())
        onView(withId(R.id.component_edit))
            .perform(typeText("KIA7805"), closeSoftKeyboard())
        onView(withId(R.id.save_component_fab)).perform(click())
        onView(withText("KIA7805")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @SearchTest
    @Test
    fun switchFromSearchToPopularAndBack_quotesNotDisplayed() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)

        moveToSearch()
        moveToPopular()
        moveToSearch()

        onView(withText("\"\"")).check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @SearchTest
    @Test
    fun searchModeHintDisplayed_rotateDevice_queriesNotDisplayed() {
        val activityScenario = launchAndMonitorMainActivity(dataBindingIdlingResource)
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }

        moveToSearch()
        rotateDeviceToLandscape(activity, activityScenario, dataBindingIdlingResource)

        onView(withText("\"\"")).check(ViewAssertions.doesNotExist())

        rotateDeviceToPortrait(activity, activityScenario, dataBindingIdlingResource)

        onView(withText("\"\"")).check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }
}