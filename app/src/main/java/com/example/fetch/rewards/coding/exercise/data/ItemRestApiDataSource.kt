package com.example.fetch.rewards.coding.exercise.data

import com.example.fetch.rewards.coding.exercise.utility.getRetrofitBuilder
import javax.inject.Inject

interface ItemDataSource {
  suspend fun getItems(): Result<List<Item>>
}

class ItemRestApiDataSource @Inject constructor(private val itemAPI: ItemService): ItemDataSource {
  override suspend fun getItems(): Result<List<Item>> {
    val response = try { itemAPI.getItems() } catch (e: Exception) { return Result.failure(e) }

    if (!response.isSuccessful) {
      val errorResponse = response.errorBody()?.string() ?: "Issue found in response"
      return Result.failure(Exception(errorResponse))
    }

    val responseBody = response.body()
    return if (responseBody != null) Result.success(responseBody) else Result.failure(Exception("Empty response body"))
  }
}

fun getItemService(): ItemService = getRetrofitBuilder().create(ItemService::class.java)