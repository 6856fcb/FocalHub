package com.focalstudio.focalhub.view.composables

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.unit.dp
import com.focalstudio.focalhub.data.model.App
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppGrid(apps: List<App>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.padding(16.dp)
    ) {
        items(apps) { app ->
            AppItem(app)
        }
    }
}

@Composable
fun AppItem(app: App) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(64.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        val painter: Painter = rememberDrawablePainter(drawable = app.icon)
        Image(
            painter = painter,
            contentDescription = app.name,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = app.name,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}