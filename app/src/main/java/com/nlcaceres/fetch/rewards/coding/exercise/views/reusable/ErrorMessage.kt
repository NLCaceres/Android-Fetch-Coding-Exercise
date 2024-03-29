package com.nlcaceres.fetch.rewards.coding.exercise.views.reusable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A simple error message elevated both tonally and with shadow above the composition */
@Composable
fun ErrorMessage(errorMessage: String, modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxSize()) {
        Surface(modifier.then(Modifier.padding(25.dp, 0.dp)), RoundedCornerShape(15.dp),
            MaterialTheme.colorScheme.errorContainer, tonalElevation = 10.dp, shadowElevation = 5.dp
        ) {
            Text(errorMessage, Modifier.padding(25.dp, 30.dp),
                fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorMessagePreview() {
    // Works well even with a nested Box
    Box(Modifier.fillMaxSize()) {
        ErrorMessage("Sorry! Currently experiencing issues!", Modifier.align(BiasAlignment(0f, -0.15f)))
    }
}