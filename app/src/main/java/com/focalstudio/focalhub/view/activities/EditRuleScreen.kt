package com.focalstudio.focalhub.view.activities
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.focalstudio.focalhub.view.viewModel.RulesManagerViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextField
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsSwitch
import com.focalstudio.focalhub.data.model.DisplayRule
import com.google.android.material.chip.ChipGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRuleScreen(
    navController: NavController,
    viewModel: RulesManagerViewModel,
    rule: DisplayRule
) {
    var name by remember { mutableStateOf(rule.name) }
    var isActive by remember { mutableStateOf(rule.isActive) }
    var isBlacklist by remember { mutableStateOf(rule.isBlacklist) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Rule") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                }
            )
        }
    ) { paddingValues ->
        SettingsGroup(
            modifier = Modifier,
            title = { Text(rule.name) },
            contentPadding = paddingValues,
        ) {
            SettingsSwitch(
                state = isActive,
                title = { Text(text = "Rule active") },
                subtitle = {},
                modifier = Modifier,
                enabled = true,
                icon = {},
                onCheckedChange = { newState: Boolean ->
                    isActive = newState
                    viewModel.updateRuleIsActive(rule.id, isActive)
                },
            )
            SettingsSwitch(
                state = isBlacklist,
                title = { Text(text = "Blacklist Mode") },
                subtitle = { Text(text = "Only exclude selected apps") },
                modifier = Modifier,
                enabled = true,
                icon = {},
                onCheckedChange = { newState: Boolean ->
                    isBlacklist = newState
                    viewModel.updateRuleIsBlacklist(rule.id, isBlacklist)
                },
            )
        }
    }
}

