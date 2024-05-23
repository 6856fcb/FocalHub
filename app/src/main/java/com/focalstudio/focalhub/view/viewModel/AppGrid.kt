package com.focalstudio.focalhub.view.viewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focalstudio.focalhub.data.model.App
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppGrid(viewModel: LauncherViewModel, context: Context) {
    val apps by viewModel.appsList.collectAsState()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.padding(16.dp)
    ) {
        items(apps) { app ->
            AppItem(app, context, isVibrationEnabled, viewModel::provideFeedback)
        }
    }
}

@Composable
fun AppItem(app: App, context: Context, isVibrationEnabled: Boolean, provideFeedback: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(64.dp)
            .clickable(
                onClick = {
                    if (isVibrationEnabled) {
                        provideFeedback()
                    }
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    launchIntent?.let { context.startActivity(it) }
                }
            ),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        val painter: Painter = rememberDrawablePainter(drawable = app.icon)
        Image(
            painter = painter,
            contentDescription = app.name,
            modifier = Modifier
                .size(54.dp)
                .background(color = Color.White, shape = androidx.compose.foundation.shape.CircleShape) // For ripple effect
                .padding(0.dp)
        )
        Text(
            text = app.name,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
