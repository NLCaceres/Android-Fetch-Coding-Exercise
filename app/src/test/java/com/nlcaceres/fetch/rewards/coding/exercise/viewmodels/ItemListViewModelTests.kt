package com.nlcaceres.fetch.rewards.coding.exercise.viewmodels

import com.nlcaceres.fetch.rewards.coding.exercise.data.Item
import com.nlcaceres.fetch.rewards.coding.exercise.data.ItemRepository
import com.nlcaceres.fetch.rewards.coding.exercise.helpers.MainDispatcherRule
import com.nlcaceres.fetch.rewards.coding.exercise.views.itemList.ItemListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class ItemListViewModelTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Test fun testItemListValueFiltering() = runTest {
        val itemList = listOf(
            Item(1, "Foo", 1), Item(2, null, 1),
            Item(3, "Bar", 1), Item(4, "", 1),
            Item(5, "Fizz", 1), Item(6, "  ", 1)
        )
        val itemRepository = mock<ItemRepository> { onBlocking { getItems() } doReturn flow { emit(itemList) } }
        val itemListViewModel = ItemListViewModel(itemRepository, mainDispatcherRule.testDispatcher)
        // Double-check the itemList flow HASN'T started, and therefore is empty
        assertEquals(itemListViewModel.itemListFlow.value.entries.size, 0)

        // BackgroundScope automatically cancels coroutines created in its launch() when the test finishes
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            itemListViewModel.itemListFlow.collect()
        }
        // WHEN the itemListFlow encounters any Items without a name (null, empty or blank)
        val collectedItemList = itemListViewModel.itemListFlow.value.getValue(1)
        // THEN it'll remove those, leaving only Items with names, in this case, "Foo", "Bar", and "Fizz"
        assertEquals(3, collectedItemList.size)
        collectedItemList.forEach { assertFalse(it.name.isNullOrBlank()) } // Double checks no items have null/blank names
    }
    @Test fun testItemListSorting() = runTest {
        val itemList = listOf(
            Item(1, "Foo", 2), Item(2, "foo", 2),
            Item(3, "Bar", 2), Item(4, "Car", 1),
            Item(5, "Fizz", 1), Item(6, "Far", 3)
        )
        val itemRepository = mock<ItemRepository> { onBlocking { getItems() } doReturn flow { emit(itemList) } }
        val itemListViewModel = ItemListViewModel(itemRepository, mainDispatcherRule.testDispatcher)
        assertEquals(itemListViewModel.itemListFlow.value.entries.size, 0)
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            itemListViewModel.itemListFlow.collect()
        }
        // WHEN the itemListFlow is sorted into its map
        var expectedListID = 1
        // THEN it is sorted by listID into an entry of (listID to itemList). Ex: [(1 to itemList), (2 to itemList), ...]
        itemListViewModel.itemListFlow.value.keys.forEach { listID -> assertEquals(expectedListID++, listID) }

        // WHEN the itemListFlow sorts the itemList values in each entry
        val itemListOne = itemListViewModel.itemListFlow.value.getValue(1)
        // THEN the itemList is sorted alphabetically by name, "Car" then "Fizz"
        assertEquals(2, itemListOne.size)
        assertEquals("Car", itemListOne[0].name)
        assertEquals("Fizz", itemListOne[1].name)

        val itemListTwo = itemListViewModel.itemListFlow.value.getValue(2)
        assertEquals(3, itemListTwo.size)
        assertEquals("Bar", itemListTwo[0].name)
        // AND in cases where the names are the same, the sort is stable so relative positioning remains the same
        assertEquals("Foo", itemListTwo[1].name)
        assertEquals("foo", itemListTwo[2].name)

        val itemListThree = itemListViewModel.itemListFlow.value.getValue(3)
        assertEquals(1, itemListThree.size)
        assertEquals("Far", itemListThree[0].name)
    }
    @Test fun testItemListGrouping() = runTest {
        // EXPECTING this list to get grouped into 3 ListID groups, #1 will be 1 item, #2 will be 2 items, and #3 will be 3 items
        val itemList = listOf(
            Item(2, "Foo", 1), Item(1, "Bar", 2),
            Item(3, "Fizz", 2), Item(6, "Buzz", 3),
            Item(4, "Abc", 3), Item(8, "Def", 3)
        )
        val itemRepository = mock<ItemRepository> { onBlocking { getItems() } doReturn flow { emit(itemList) } }
        val itemListViewModel = ItemListViewModel(itemRepository, mainDispatcherRule.testDispatcher)
        assertEquals(itemListViewModel.itemListFlow.value.entries.size, 0)
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            itemListViewModel.itemListFlow.collect()
        }
        // WHEN the itemListFlow groups Items by their listID
        var expectedListID = 1
        var expectedGroupSize = 1
        itemListViewModel.itemListFlow.value.entries.forEach { (listID, items) ->
            // THEN all items in each grouping will have the same listID
            items.forEach { assertEquals(expectedListID, it.listId) }
            assertEquals(expectedListID++, listID)
            assertEquals(expectedGroupSize++, items.size) // ListID Group #1 will have 1 Item, #2 will have 2, and #3 will have 3 items
        }
    }
    @Test fun testItemLoading() = runTest {
        val itemRepository = mock<ItemRepository> { onBlocking { getItems() } doReturn flow { emit(emptyList()) } }
        val itemListViewModel = ItemListViewModel(itemRepository, mainDispatcherRule.testDispatcher)
        assertEquals(itemListViewModel.itemListFlow.value.entries.size, 0)
        // Double-check "isLoading" begins as false
        assertFalse(itemListViewModel.isLoading.value)
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            // Since coroutines suspend until flows complete,
            // I can leverage that stateFlows never complete to collect the flow like normal and assert all "isLoading" updates
            var loadingUpdated = 0
            // EXPECTING: ----false----true----false-->
            itemListViewModel.isLoading.collect {
                if (loadingUpdated++ % 2 == 0) {
                    assertFalse(itemListViewModel.isLoading.value)
                }
                else {
                    assertTrue(itemListViewModel.isLoading.value)
                }
            }
        }
        // In a separate coroutine, I can then launch the itemList stateFlow to begin updating "isLoading" in the process
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            itemListViewModel.itemListFlow.collect()
        }
    }
    @Test fun testRefreshingItems() = runTest {
        val firstItemList = listOf(
            Item(2, "Foo", 1), Item(1, "Bar", 2),
            Item(3, "Fizz", 2), Item(6, "Buzz", 3),
            Item(4, "Abc", 3), Item(8, "Def", 3)
        )
        val itemRepository = mock<ItemRepository> { onBlocking { getItems() } doReturn flow { emit(firstItemList) } }
        val itemListViewModel = ItemListViewModel(itemRepository, mainDispatcherRule.testDispatcher)
        assertEquals(0, itemListViewModel.itemListFlow.value.entries.size)

        var itemListUpdates = 0 // Used to count how many values have been emitted
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            itemListViewModel.itemListFlow.collect { itemListUpdates++ }
        }
        // WHEN the itemFlowList StateFlow is initially collected, THEN TWO values are emitted and received: --[]---[1: [], 2: [], 3: []]--->
        assertEquals(2, itemListUpdates)
        // AND itemListFlow's most recent value is firstItemList, which is grouped into 3 key-val pairs (list #1, #2 and #3)
        assertEquals(3, itemListViewModel.itemListFlow.value.entries.size)

        val secondItemList = listOf(Item(2, "Car", 1), Item(1, "Fox", 2))
        whenever(itemRepository.getItems()).thenReturn(flow { emit(secondItemList) })
        // WHEN a refresh request is made
        itemListViewModel.refreshItems()
        // THEN the flow() is recollected, emitting a 3rd value
        assertEquals(3, itemListUpdates)
        // AND itemListFlow's new value is now secondItemList, grouped into 2 key-val pairs (list #1 and #2)
        assertEquals(2, itemListViewModel.itemListFlow.value.entries.size)
    }
    @Test fun testItemFlowThrows() = runTest {
        // In order for itemListFlow to catch() anything, the mock flow must throw, NOT getItems(), hence this doReturn below
        val itemRepository = mock<ItemRepository> { onBlocking { getItems() } doReturn flow { throw Exception() } }
        val itemListViewModel = ItemListViewModel(itemRepository, mainDispatcherRule.testDispatcher)
        assertEquals(itemListViewModel.itemListFlow.value.entries.size, 0)
        // "errorMessage" starts empty indicating nothing has failed
        assertTrue(itemListViewModel.errorMessage.value.isEmpty())
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            itemListViewModel.itemListFlow.collect()
            itemListViewModel.errorMessage.collect()
        }
        // WHEN the Repository fails and throws an error, THEN the itemListFlow value stays empty
        assertEquals(itemListViewModel.itemListFlow.value.entries.size, 0)
        // AND the viewModel updates its "errorMessage" to reflect something went wrong
        assertEquals(itemListViewModel.errorMessage.value, "Sorry! We seem to be having trouble fetching your list of items!")
    }
}