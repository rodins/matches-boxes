package com.sergeyrodin.matchesboxes.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DB_NAME = "test-db"

private val BAG = Bag(1, "Bag")
private val SET = MatchesBoxSet(1, "Set", BAG.id)
private val BOX = MatchesBox(1, "Box", SET.id)
private val COMPONENT = RadioComponent(1, "Component", 3, BOX.id, true)
private val HISTORY = History(1, COMPONENT.id, COMPONENT.quantity, 0)

@RunWith(AndroidJUnit4::class)
@SmallTest
class MigrationsTest {
    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RadioComponentsDatabase::class.java.canonicalName)

    @Test
    fun migrationFrom1To2_containsCorrectData(){
        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 1)
        insertComponent(COMPONENT.id, COMPONENT.name, COMPONENT.quantity, COMPONENT.matchesBoxId, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            2,
            false,
            RadioComponentsDatabase.MIGRATION_1_2
        )

        val component = getMigratedRoomDatabase().radioComponentsDatabaseDao.getRadioComponentByIdBlocking(
            COMPONENT.id)
        assertThat(component.id, `is`(COMPONENT.id))
        assertThat(component.name, `is`(COMPONENT.name))
        assertThat(component.quantity, `is`(COMPONENT.quantity))
        assertThat(component.matchesBoxId, `is`(COMPONENT.matchesBoxId))
        // New column default value
        assertThat(component.isBuy, `is`(false))
    }

    @Test
    fun migrationFrom2To3_containsCorrectData(){
        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 2)
        insertComponent(COMPONENT, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            3,
            false,
            RadioComponentsDatabase.MIGRATION_2_3
        )

        val dao = getMigratedRoomDatabase().radioComponentsDatabaseDao
        dao.insertHistoryBlocking(HISTORY)
        val history = dao.getHistoryByIdBlocking(HISTORY.id)
        assertThat(history, `is`(HISTORY))
    }

    private fun getMigratedRoomDatabase(): RadioComponentsDatabase {
        val database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RadioComponentsDatabase::class.java,
            TEST_DB_NAME)
            .addMigrations(RadioComponentsDatabase.MIGRATION_1_2)
            .build()
        migrationTestHelper.closeWhenFinished(database)
        return database
    }

    private fun insertComponent(id: Int, name: String, quantity: Int, boxId: Int, db: SupportSQLiteDatabase) {
        insertBagSetBox(db)

        val componentContentValues = ContentValues()
        componentContentValues.put("id", id)
        componentContentValues.put("name", name)
        componentContentValues.put("quantity", quantity)
        componentContentValues.put("matches_box_id", boxId)
        db.insert("radio_components", SQLiteDatabase.CONFLICT_REPLACE, componentContentValues)
    }

    private fun insertComponent(component: RadioComponent, db: SupportSQLiteDatabase) {
        insertBagSetBox(db)

        val componentContentValues = ContentValues()
        componentContentValues.put("id", component.id)
        componentContentValues.put("name", component.name)
        componentContentValues.put("quantity", component.quantity)
        componentContentValues.put("matches_box_id", component.matchesBoxId)
        componentContentValues.put("buy", component.isBuy)
        db.insert("radio_components", SQLiteDatabase.CONFLICT_REPLACE, componentContentValues)
    }

    private fun insertBagSetBox(db: SupportSQLiteDatabase) {
        val bagContentValues = ContentValues()
        bagContentValues.put("id", BAG.id)
        bagContentValues.put("name", BAG.name)
        db.insert("bags", SQLiteDatabase.CONFLICT_REPLACE, bagContentValues)

        val setContentValues = ContentValues()
        setContentValues.put("id", SET.id)
        setContentValues.put("name", SET.name)
        setContentValues.put("bag_id", SET.bagId)
        db.insert("matches_box_sets", SQLiteDatabase.CONFLICT_REPLACE, setContentValues)

        val boxContentValues = ContentValues()
        boxContentValues.put("id", BOX.id)
        boxContentValues.put("name", BOX.name)
        boxContentValues.put("matches_box_set_id", BOX.matchesBoxSetId)
        db.insert("matches_boxes", SQLiteDatabase.CONFLICT_REPLACE, boxContentValues)
    }
}