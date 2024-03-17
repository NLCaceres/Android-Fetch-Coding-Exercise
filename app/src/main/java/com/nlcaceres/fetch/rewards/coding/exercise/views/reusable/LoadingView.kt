package com.nlcaceres.fetch.rewards.coding.exercise.views.reusable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Displays a CircularProgressIndicator with text that changes every 5 seconds it is in the composition
 * It is VERY important that this Composable is wrapped in an if-block to be conditionally un-rendered
 * or simply removed from the UI eventually. If it is not removed, then its launchedEffect will continue forever
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    var loadingIndex by remember { mutableStateOf(0) }
    val loadingTextValues = listOf("Loading!", "Still working on it!", "Should finish loading soon!")
    val loadingText = loadingTextValues[loadingIndex]
    // Setting LaunchedEffect = true ensures its coroutine is kept alive UNTIL this LoadingView leaves the composition
    // Similar to while (true), it's important that LoadingView is EVENTUALLY REMOVED
    LaunchedEffect(true) { // LaunchedEffect SHOULDN'T block the UI
        while (true) {
            delay(5000)
            if (loadingIndex < 2) { loadingIndex++ } else { loadingIndex = 0 }
        }
    }

    Column(modifier.then(Modifier), Arrangement.Center, Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            Modifier.size(width = 70.dp, height = 70.dp), strokeWidth = 5.dp,
            color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primaryContainer
        )
        Text(loadingText, Modifier.padding(top = 15.dp), fontSize = 24.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    Box(Modifier.fillMaxSize()) {
        LoadingView(Modifier.align(Alignment.Center))
    }
}