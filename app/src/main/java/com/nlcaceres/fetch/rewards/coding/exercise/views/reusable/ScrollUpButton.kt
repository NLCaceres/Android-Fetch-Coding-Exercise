package com.nlcaceres.fetch.rewards.coding.exercise.views.reusable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nlcaceres.fetch.rewards.coding.exercise.R

/** A Circular Floating Action Button with an Upward Arrow Icon, laid out in the top center area of the composition
 * Its onClick parameter should run a func that scrolls up the parent or nearby list composition
 * */
@Composable
fun ScrollUpButton(onClick: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick, Modifier.size(50.dp).align(BiasAlignment(0f, -0.75f)),
            CircleShape, containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                painterResource(R.drawable.ic_arrow_upward), stringResource(R.string.scroll_up_button),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ScrollUpButtonPreview() {
    ScrollUpButton {  }
}