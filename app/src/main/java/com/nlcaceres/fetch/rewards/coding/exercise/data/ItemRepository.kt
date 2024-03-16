package com.nlcaceres.fetch.rewards.coding.exercise.data

import com.nlcaceres.fetch.rewards.coding.exercise.utility.AppModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface ItemRepository {
  fun getItems(): Flow<List<Item>>
}

class AppItemRepository @Inject constructor(@AppModule.RestApiDataSource private val itemRestApiDataSource: ItemDataSource,
                                            private val ioDispatcher: CoroutineDispatcher): ItemRepository {

  override fun getItems(): Flow<List<Item>> {
    return flow {
      emit(emptyList())

      val itemsResult = itemRestApiDataSource.getItems()
      emit(itemsResult.getOrDefault(emptyList()))

      if (itemsResult.isFailure) { throw itemsResult.exceptionOrNull()!! }

    }.flowOn(ioDispatcher)
  }

}