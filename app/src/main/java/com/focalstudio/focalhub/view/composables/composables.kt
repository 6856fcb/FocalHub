import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.focalstudio.focalhub.utils.log
import com.focalstudio.focalhub.view.viewModel.AppUsageViewModel
import com.focalstudio.focalhub.view.viewModel.RulesManagerViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppSelectionDialog(
    allApps: List<App>,
    initialSelectedApps: List<String>,
    onDismissRequest: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val selectedApps = remember { mutableStateListOf<String>().apply { addAll(initialSelectedApps) } }
    val searchQuery = remember { mutableStateOf(TextFieldValue()) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.padding(vertical = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text(text = "Search App") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    val filteredApps: List<App> = allApps.filter { app ->
                        app.name.contains(searchQuery.value.text, ignoreCase = true)
                    }

                    items(filteredApps.sortedBy { it.name }) { app ->
                        AppItem(
                            app = app,
                            isSelected = selectedApps.contains(app.packageName),
                            onSelect = {
                                if (selectedApps.contains(app.packageName)) {
                                    selectedApps.remove(app.packageName)
                                } else {
                                    selectedApps.add(app.packageName)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(selectedApps)
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RulesAppSelectionDialog(
    viewModel: RulesManagerViewModel,
    ruleId: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val allApps by remember { viewModel.appsList }
    val rule = viewModel.getRuleById(ruleId)
    val initialSelectedApps = rule.appList ?: emptyList()

    AppSelectionDialog(
        allApps = allApps,
        initialSelectedApps = initialSelectedApps,
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppUsageSelectionDialog(
    viewModel: AppUsageViewModel,
    ruleId: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val allApps by remember { viewModel.appsList }
    val rule = viewModel.getUsageRuleById(ruleId)
    val initialSelectedApps = rule?.appList ?: emptyList()

    AppSelectionDialog(
        allApps = allApps,
        initialSelectedApps = initialSelectedApps,
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppSearchDialog(
    viewModel: HomeScreenViewModel,
    context: Context,
    onDismissRequest: () -> Unit
) {
    val allApps by remember { viewModel.allAppsList }
    val searchQuery = remember { mutableStateOf(TextFieldValue()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Show the keyboard when the dialog is displayed
    DisposableEffect(Unit) {
        keyboardController?.show()
        onDispose {
            keyboardController?.hide()
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .padding(vertical = 16.dp)
                ,
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text(text = "Search App") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    val filteredApps: List<App> = allApps.filter { app ->
                        app.name.contains(searchQuery.value.text, ignoreCase = true)
                    }

                    if (searchQuery.value.text.isNotEmpty()) {
                        items(filteredApps.sortedBy { it.name }) { app ->
                            SearchAppItem(app, context, viewModel)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchAppItem(app: App, context: Context, viewModel: HomeScreenViewModel) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.appIconClicked(app, context)
                    }
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter: Painter = rememberDrawablePainter(drawable = app.icon)
        Image(
            painter = painter,
            contentDescription = app.name,
            modifier = Modifier
                .size(46.dp)
                .background(color = Color.White, shape = CircleShape) // For ripple effect
                .padding(0.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(app.name)
    }
}
@Composable
fun AppItem(app: App, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(app.name)
    }
}
