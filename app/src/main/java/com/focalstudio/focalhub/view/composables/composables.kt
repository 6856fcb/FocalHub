import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.focalstudio.focalhub.R
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.view.viewModel.RulesManagerViewModel


@Composable
fun AppSelectionDialog(
    viewModel: RulesManagerViewModel,
    ruleId: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val allApps by remember { viewModel.appsList} // Use the state from ViewModel
    val rule = viewModel.getRuleById(ruleId)
    val selectedApps = remember { mutableStateListOf<String>() }
    val searchQuery = remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(rule) {
        rule?.appList?.let { selectedApps.addAll(it) }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.padding(vertical = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text(text = "Search App")},
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    //placeholder = { Text(text = "Search App")}
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f) // Add weight to occupy available space
                ) {
                    val filteredApps: List<App> = allApps.filter { app ->
                        app.name.contains(searchQuery.value.text, ignoreCase = true)
                    }



                    items(filteredApps.sortedBy { it.name }) { app -> // Sort apps alphabetically
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
                    modifier = Modifier.fillMaxWidth().weight(0.1f) // Add weight to occupy available space
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(selectedApps) }) {
                        Text("Confirm")
                    }
                }
            }
        }
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
