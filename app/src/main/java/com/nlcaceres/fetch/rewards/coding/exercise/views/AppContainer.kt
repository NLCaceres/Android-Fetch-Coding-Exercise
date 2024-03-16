package com.nlcaceres.fetch.rewards.coding.exercise.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nlcaceres.fetch.rewards.coding.exercise.ui.theme.FetchRewardsCodingExerciseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContainer(content: @Composable (PaddingValues) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Item List", fontSize = 40.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }) { innerPadding ->
            content(innerPadding)
        }
    }
}

@Preview(widthDp = 400, heightDp = 800, showBackground = true, apiLevel = 33)
@Composable
fun HomeViewPreview() {
    FetchRewardsCodingExerciseTheme {
        AppContainer { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                Text("Hello World!")
            }
        }
    }
}