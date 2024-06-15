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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, context: Context, viewModel: HomeScreenViewModel, lifecycleOwner: LifecycleOwner) {
   /*androidx.compose.material3.Surface {
       modifier = Modifier.fillMaxSize().background(Color.Black)
   }*/




    val apps by remember { mutableStateOf(viewModel.appsList) }
    var showAppSearchDialog by remember { mutableStateOf(false) }
    lifecycleOwner.lifecycle.addObserver(viewModel)
    Scaffold(
        topBar = {

            TopAppBar(

                title = { Text("Home",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp)},
                actions = {
                    IconButton(onClick = { navController.navigate("settingsScreen") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", Modifier.size(30.dp))
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { showAppSearchDialog = true }) {
                Icon(Icons.Filled.Search, contentDescription = "Search Apps", tint = Color.Magenta)
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
        modifier = Modifier.padding(5.dp, bottom = 10.dp)
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
            .padding(6.dp)
            .width(80.dp)
            .clickable(
                onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.appIconClicked(app, context)
                    }
                }
            ),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        val painter: Painter = rememberDrawablePainter(drawable = app.icon)
        Image(
            painter = painter,
            contentDescription = app.name,
            modifier = Modifier
                .size(60.dp)

        )
        Text(
            text = app.name,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
