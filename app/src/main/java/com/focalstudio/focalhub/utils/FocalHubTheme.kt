package com.focalstudio.focalhub.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FocalHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        // Define your color scheme, typography, and other theme parameters here
        content = content
    )
}