package com.nlcaceres.fetch.rewards.coding.exercise.views.itemList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.nlcaceres.fetch.rewards.coding.exercise.data.Item
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlcaceres.fetch.rewards.coding.exercise.views.reusable.ErrorMessage
import com.nlcaceres.fetch.rewards.coding.exercise.views.reusable.LoadingView
import com.nlcaceres.fetch.rewards.coding.exercise.views.reusable.ScrollUpButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListView(modifier: Modifier = Modifier, viewModel: ItemListViewModel = viewModel()) {
  val items by viewModel.itemListFlow.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

  val pullToRefreshState = rememberPullToRefreshState(100.dp)
  if (pullToRefreshState.isRefreshing) {
    // This effect works slightly different, running if the pullToRefresh indicator is pulled, every time
    LaunchedEffect(true) { // Despite normally launchedEffect(true) would ONLY run once EVER
      viewModel.refreshItems()
    }
  }
  LaunchedEffect(isLoading) { // Calls this effect for every change to loading state
    if (isLoading) { return@LaunchedEffect } // Early return to let indicator stay visible if fetch is still loading
    pullToRefreshState.endRefresh() // Remove indicator when loading finishes/false
  }

  Box(modifier.then(Modifier.fillMaxSize().nestedScroll(pullToRefreshState.nestedScrollConnection))) {
    ItemList(items)
    PullToRefreshContainer(pullToRefreshState, Modifier.align(Alignment.TopCenter))

    if (!pullToRefreshState.isRefreshing && isLoading) { // Only show loading indicator OR the refresh indicator, NEVER both
      LoadingView(Modifier.align(BiasAlignment(0f, -0.15f)))
    }
    else if (errorMessage.isNotBlank()) {
      ErrorMessage(errorMessage, Modifier.align(BiasAlignment(0f, -0.15f)))
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemList(itemListMap: Map<Int, List<Item>>) {
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  // derivedState helps limit state updates of listState (similar to Kotlin Flow's distinctUntilChanged)
  val showScrollUpButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

  LazyColumn(state = listState) {
    itemListMap.forEach { (listID, itemList) ->
      stickyHeader {
        Text(
          "Items in List #$listID", fontSize = 30.sp, fontWeight = FontWeight.SemiBold,
          modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer).padding(start = 25.dp, top = 20.dp, bottom = 10.dp)
        )
      }

      itemsIndexed(itemList, key = { _, item -> item.listId to item.id }) { index, item ->
        ItemRow(item.name ?: "Item #${item.id}")
        if (index < itemList.lastIndex) {
          HorizontalDivider(thickness = 1.dp, color = Color.Black)
        }
      }
    }
  }
  AnimatedVisibility(showScrollUpButton) {
    ScrollUpButton(onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } })
  }
}

@Preview(showBackground = true)
@Composable
fun ItemListViewPreview() {
  val itemListOne = listOf(Item(123, "Item 123", 1), Item(234, "Item 234", 1))
  val itemListTwo = listOf(Item(345, "Item 345", 2), Item(456, "Item 456", 2))
  val itemListMap = mapOf(1 to itemListOne, 2 to itemListTwo)
  ItemList(itemListMap)
}