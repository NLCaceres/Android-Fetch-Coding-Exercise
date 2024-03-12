package com.example.fetch.rewards.coding.exercise.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ItemRepository {
  fun getItems(): Flow<List<Item>>
}

class AppItemRepository(private val itemRestApiDataSource: ItemDataSource = ItemRestApiDataSource(),
                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): ItemRepository {

  override fun getItems(): Flow<List<Item>> {
    return flow {
      emit(emptyList())

      val itemsResult = itemRestApiDataSource.getItems()
      emit(itemsResult.getOrDefault(emptyList()))

      if (itemsResult.isFailure) { throw itemsResult.exceptionOrNull()!! }

    }.flowOn(ioDispatcher)
  }

}