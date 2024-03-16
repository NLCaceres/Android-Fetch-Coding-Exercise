package com.nlcaceres.fetch.rewards.coding.exercise.views.itemList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.nlcaceres.fetch.rewards.coding.exercise.data.Item
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlcaceres.fetch.rewards.coding.exercise.views.reusable.ErrorMessage
import com.nlcaceres.fetch.rewards.coding.exercise.views.reusable.LoadingView

@Composable
fun ItemListView(modifier: Modifier, viewModel: ItemListViewModel = viewModel()) {
  val items by viewModel.itemListFlow.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

  Box(modifier.then(Modifier.fillMaxSize())) {
    ItemList(items)

    if (isLoading) {
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
  LazyColumn {
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
}

@Preview(showBackground = true)
@Composable
fun ItemListViewPreview() {
  val itemListOne = listOf(Item(123, "Item 123", 1), Item(234, "Item 234", 1))
  val itemListTwo = listOf(Item(345, "Item 345", 2), Item(456, "Item 456", 2))
  val itemListMap = mapOf(1 to itemListOne, 2 to itemListTwo)
  ItemList(itemListMap)
}