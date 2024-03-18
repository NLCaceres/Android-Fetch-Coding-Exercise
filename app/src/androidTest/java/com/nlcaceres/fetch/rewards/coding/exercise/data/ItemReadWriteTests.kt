package com.nlcaceres.fetch.rewards.coding.exercise.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ItemReadWriteTests {
    private lateinit var itemDao: ItemDao
    private lateinit var db: AppDatabase
    private var dispatcher = UnconfinedTestDispatcher()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        itemDao = db.itemDao()
    }

    @After
    @Throws(IOException::class) // This annotation provides Java interop so Java can catch Kotlin's unchecked exceptions
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndGetAllItems() = runTest(dispatcher.scheduler) {
        val itemList = listOf(Item(1, "Foo", 1), Item(2, "Bar", 1))
        // WHEN a list of 2 Items are inserted into the database
        itemDao.insertAll(*itemList.toTypedArray())
        val allItems = itemDao.getAll()
        // THEN 2 are returned when ALL are requested
        assertEquals(2, allItems.size)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertItemsWithConflict() = runTest(dispatcher.scheduler) {
        val itemList = listOf(Item(1, "Foo", 1), Item(2, "Bar", 1))
        // WHEN a list of 2 Items are inserted into the database
        itemDao.insertAll(*itemList.toTypedArray())
        val allItems = itemDao.getAll()
        // THEN 2 are returned when ALL are requested
        assertEquals(2, allItems.size)
        assertEquals("Foo", allItems[0].name)
        assertEquals("Bar", allItems[1].name)

        // WHEN a new Item is inserted into the database with a matching ID
        itemDao.insertAll(Item(2, "Car", 1))
        val newAllItems = itemDao.getAll()
        // THEN any of its fields are updated
        assertEquals(2, newAllItems.size)
        assertEquals("Foo", newAllItems[0].name) // "Foo" remains the same
        assertEquals("Car", newAllItems[1].name) // "Bar" is now "Car"

        // WHEN an Item is inserted into the database with a matching name
        itemDao.insertAll(Item(3, "Foo", 1))
        val anotherItemList = itemDao.getAll()
        // THEN it is inserted like normal, no other rows or their fields are updated
        assertEquals(3, anotherItemList.size)
        assertEquals("Foo", anotherItemList[0].name)
        assertEquals("Car", anotherItemList[1].name)
        assertEquals("Foo", anotherItemList[2].name)

        // WHEN an Item is inserted into the database with a matching ID BUT different values
        itemDao.insertAll(Item(3, "Abc", 2))
        val finalAllItems = itemDao.getAll()
        // THEN ALL of its fields are updated except its ID
        assertEquals(3, finalAllItems.size)
        assertEquals("Foo", finalAllItems[0].name) // Remains the same
        assertEquals("Car", finalAllItems[1].name) // Remains the same
        assertEquals("Abc", finalAllItems[2].name) // This "Foo" becomes "Abc
        assertEquals(2, finalAllItems[2].listId) // AND its list ID goes from 1 to 2
    }

    @Test
    @Throws(Exception::class)
    fun testGetItemsWithNames() = runTest(dispatcher.scheduler) {
        val itemList = listOf(
            Item(1, "Foo", 1), Item(2, null, 1),
            Item(3, "Bar", 1), Item(4, "", 1),
            Item(5, "Fizz", 1), Item(6, "  ", 1)
        )
        itemDao.insertAll(*itemList.toTypedArray())
        // WHEN the DAO gets Items only with names
        val onlyItemsWithNames = itemDao.getAllWithNames()
        // THEN any null or blank string Items are filtered out, leaving behind only actually named Items
        assertEquals(3, onlyItemsWithNames.size)
        onlyItemsWithNames.forEach { assertFalse(it.name.isNullOrBlank()) }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteItems() = runTest(dispatcher.scheduler) {
        val itemList = listOf(Item(1, "Foo", 1), Item(2, "Bar", 1), Item(3, "Fizz", 2))
        itemDao.insertAll(*itemList.toTypedArray())
        val allItems = itemDao.getAll()
        assertEquals(3, allItems.size)

        // WHEN 3 items have been inserted into the DB and an item with matching ID is used for deletion
        itemDao.deleteItems(Item(2, "", 0))
        val remainingItems = itemDao.getAll()
        // THEN deletion occurs. It DOESN'T need to be the same instance used to create that Item
        assertEquals(2, remainingItems.size) // 3 Items in the DB becomes 2 Items

        // WHEN 2 items are used for deletion (that are the exact same instance used for creation)
        itemDao.deleteItems(itemList[0], itemList[2])
        val finalItemList = itemDao.getAll()
        // THEN deletion also occurs
        assertEquals(0, finalItemList.size) // 2 Items in the DB now reduced to 0
    }
}