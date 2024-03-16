package com.nlcaceres.fetch.rewards.coding.exercise.views.itemList

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemRow(itemName: String) {
  Row(Modifier.fillMaxWidth()) {
    Text(itemName, Modifier.padding(20.dp, 20.dp), fontSize = 16.sp, fontWeight = FontWeight.Medium)
  }
}

@Preview(showBackground = true)
@Composable
fun ItemRowPreview() {
  ItemRow("Item #123")
}