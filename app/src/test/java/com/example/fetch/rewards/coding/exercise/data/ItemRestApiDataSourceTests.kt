package com.example.fetch.rewards.coding.exercise.data

import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.*
import org.mockito.quality.Strictness
import retrofit2.Response

@ExperimentalCoroutinesApi
class ItemRestApiDataSourceTests {
  @get:Rule
  val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

  @Test fun `Get Items from API`() = runTest {
    val itemList = listOf(Item(123, "Foo", 1), Item(321, "Bar", 2))
    val itemAPI = mock<ItemService> { onBlocking { getItems() } doReturn Response.success(itemList) }

    val itemDataSource = ItemRestApiDataSource(itemAPI)
    // WHEN a successful response is returned
    val result = itemDataSource.getItems()
    // THEN data in the response is wrapped in a Result.success()
    assertEquals(itemList, result.getOrNull()!!)
    assertEquals(Result.success(itemList), result)
    verify(itemAPI, times(1)).getItems()

    // thenThrow() ONLY works with RuntimeException in Kotlin (since it has unchecked exceptions)
    whenever(itemAPI.getItems()).thenThrow(RuntimeException("Problem with Service"))
    // WHEN the API call fails
    val failingResult = itemDataSource.getItems()
    // THEN the Result returned wraps the API's thrown exception in failure()
    assertEquals(Exception("Problem with Service").message, failingResult.exceptionOrNull()!!.message)
    // ALSO, must reset the Mock, or else it'll continue to throw (even when trying to change the mocked return)
    reset(itemAPI)

    whenever(itemAPI.getItems()).thenReturn(Response.error(400, ResponseBody.create(null, "Endpoint is invalid")))
    // WHEN the API call returns an unsuccessful response (the status code is not 200 to 299)
    val failingResponse = itemDataSource.getItems()
    // THEN a Result.failure() is generated directly containing the error body message
    assertEquals(Exception("Endpoint is invalid").message, failingResponse.exceptionOrNull()!!.message)
  }
}