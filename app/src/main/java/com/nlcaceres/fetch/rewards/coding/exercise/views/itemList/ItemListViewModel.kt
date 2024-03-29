package com.nlcaceres.fetch.rewards.coding.exercise.views.itemList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nlcaceres.fetch.rewards.coding.exercise.data.Item
import com.nlcaceres.fetch.rewards.coding.exercise.data.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ItemListViewModel @Inject constructor(private val itemRepository: ItemRepository,
                                            private val ioDispatcher: CoroutineDispatcher): ViewModel() {

  private val _isLoading = MutableStateFlow(false)
  val isLoading = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow("")
  val errorMessage = _errorMessage.asStateFlow()

  private fun constructedFlow() = itemRepository.getItems()
    .map { list ->
      list.filter { !it.name.isNullOrBlank() }
          .sortedWith(compareBy(Item::listId, { it.name?.lowercase() }))
          .groupBy(Item::listId) // Preserves order of key entry so the listID sort above helps correctly order the view
    }.flowOn(ioDispatcher)
    .onStart { _isLoading.value = true }
    .onCompletion { _isLoading.value = false } // onCompletion runs even if the upstream flows fail/throw (like a "finally" block)
    .catch { _errorMessage.value = "Sorry! We seem to be having trouble fetching your list of items!" }
  private val itemListFlowStarter = MutableStateFlow(false)
  val itemListFlow = itemListFlowStarter.flatMapLatest { constructedFlow() }.stateIn(viewModelScope, WhileSubscribed(5000), mapOf())

  fun refreshItems() {
      itemListFlowStarter.value = !itemListFlowStarter.value
  }
}