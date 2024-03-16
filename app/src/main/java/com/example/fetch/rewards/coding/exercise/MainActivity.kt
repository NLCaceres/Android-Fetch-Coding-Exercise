package com.example.fetch.rewards.coding.exercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.fetch.rewards.coding.exercise.ui.theme.FetchRewardsCodingExerciseTheme
import com.example.fetch.rewards.coding.exercise.views.AppContainer
import com.example.fetch.rewards.coding.exercise.views.itemList.ItemListView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      FetchRewardsCodingExerciseTheme {
        AppContainer { innerPadding ->
          ItemListView(Modifier.padding(innerPadding))
        }
      }
    }
  }
}
