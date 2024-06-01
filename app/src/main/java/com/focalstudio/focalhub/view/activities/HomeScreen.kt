package com.focalstudio.focalhub.view.activities

import AppSearchDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, context: Context, viewModel: HomeScreenViewModel, lifecycleOwner: LifecycleOwner) {
    val apps by remember { mutableStateOf(viewModel.appsList) }
    var showAppSearchDialog by remember { mutableStateOf(false) }
    lifecycleOwner.lifecycle.addObserver(viewModel)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { navController.navigate("settingsScreen") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAppSearchDialog = true }) {
                Icon(Icons.Filled.Search, contentDescription = "Search Apps")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(
                text = "Apps",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            AppGrid(apps, context, viewModel)
        }

        if (showAppSearchDialog) {
            AppSearchDialog(
                viewModel = viewModel,
                onDismissRequest = { showAppSearchDialog = false },
                context = context
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppGrid(
    apps: State<List<App>>,
    context: Context,
    viewModel: HomeScreenViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.padding(8.dp, bottom = 32.dp)
    ) {
        itemsIndexed(apps.value) { _, app ->
            AppItem(app, context, viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppItem(app: App, context: Context, viewModel: HomeScreenViewModel) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .width(80.dp)
            .clickable(
                onClick = {
                    viewModel.appIconClicked(app, context)
                }
            ),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        val painter: Painter = rememberDrawablePainter(drawable = app.icon)
        Image(
            painter = painter,
            contentDescription = app.name,
            modifier = Modifier
                .size(46.dp)
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
