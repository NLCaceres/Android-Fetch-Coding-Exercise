package com.example.fetch.rewards.coding.exercise.data

import com.example.fetch.rewards.coding.exercise.helpers.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class ItemRepositoryTests {
  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()
  @get:Rule
  val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

  @Mock
  lateinit var itemsCollector: FlowCollector<List<Item>>

  @Test fun `Get Items from API Data Source`() = runTest {
    val itemList = listOf(Item("123", "Foo", "1"), Item("321", "Bar", "2"))
    val itemDataSource = mock<ItemDataSource> { onBlocking { getItems() } doReturn Result.success(itemList) }
    val itemRepository = AppItemRepository(itemDataSource, mainDispatcherRule.testDispatcher)

    // WHEN itemsRepository requests data from API
    itemRepository.getItems().collect(itemsCollector)
    // THEN the dataSource's getter is called ONCE
    verify(itemDataSource, times(1)).getItems()
    // AND two emissions are made
    verifyBlocking(itemsCollector, times(2)) { emit(any()) }
    inOrder(itemsCollector) {
      // 1st emission = an emptyList, 2nd = any data returned from the API
      verifyBlocking(itemsCollector, times(1)) { emit(emptyList()) }
      verifyBlocking(itemsCollector, times(1)) { emit(itemList) }
    }
  }

  @Test fun `Fail to Get Items from API Data Source`() = runTest {
    val failureResult = Result.failure<List<Item>>(Exception("Error thrown!"))
    val itemDataSource = mock<ItemDataSource> { onBlocking { getItems() } doReturn failureResult }
    val itemRepository = AppItemRepository(itemDataSource, mainDispatcherRule.testDispatcher)

    // WHEN itemsRepository requests data from API BUT it fails
    itemRepository.getItems().catch {
      // THEN the flow is disrupted due to the Result.failure's exception being thrown
      assertEquals(it.message, "Error thrown!")
    }.collect(itemsCollector)
    // AND the dataSource's getter is called ONCE
    verify(itemDataSource, times(1)).getItems()
    // AND BOTH emissions are of empty lists
    verifyBlocking(itemsCollector, times(2)) { emit(any()) }
    verifyBlocking(itemsCollector, times(2)) { emit(emptyList()) }
  }
}