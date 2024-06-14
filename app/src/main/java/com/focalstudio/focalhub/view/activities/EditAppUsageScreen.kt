package com.focalstudio.focalhub.view.activities

import AppUsageSelectionDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.focalstudio.focalhub.data.model.UsageRule
import com.focalstudio.focalhub.view.viewModel.AppUsageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditAppUsageScreen(
    navController: NavController,
    viewModel: AppUsageViewModel,
    usageRule: UsageRule,
    context: Context
) {
    var showAppSelectionDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(usageRule.name) }
    var isCurrentlyActive by remember { mutableStateOf(usageRule.isCurrentlyActive) }
    var isRecurring by remember { mutableStateOf(usageRule.isRecurring) }
    var isDisabled by remember { mutableStateOf(usageRule.isManuallyDisabled) }
    var isEndTimeSet by remember { mutableStateOf(usageRule.isRestrictedUntilEndTime) }
    var selectedStartTime by remember { mutableStateOf(usageRule.timeWindowStartTime) }
    var selectedEndTime by remember { mutableStateOf(usageRule.timeWindowEndTime) }
    var selectedWeekdays by remember { mutableStateOf(usageRule.weekdays) }
    var isLinked by remember { mutableStateOf(usageRule.isLinkedToDisplayRule)}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Rule") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.deleteUsageRule(usageRule.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Rule")
            }
        }
    ) { paddingValues ->
        SettingsGroup(
            modifier = Modifier,
            title = { Text(usageRule.name) },
            contentPadding = paddingValues,
        ) {




            SettingsSwitch(
                state = isCurrentlyActive,
                title = { Text(text = "Rule active") },
                subtitle = {},
                modifier = Modifier,
                enabled = !isRecurring,
                icon = {},
                onCheckedChange = { newState: Boolean ->
                    isCurrentlyActive = newState
                    viewModel.setIsCurrentlyActive(ruleId = usageRule.id, newState)
                },
            )
            SettingsMenuLink(
                title = { Text(text = "Change Rule name") },
                subtitle = {},
                modifier = Modifier,
                enabled = true,
                icon = {},
                action = {},
                onClick = {},
            )
            SettingsMenuLink(
                title = { Text(text = "Choose Apps") },
                subtitle = {},
                modifier = Modifier,
                enabled = true,
                icon = {},
                action = {},
                onClick = {
                    showAppSelectionDialog = true
                },
            )
            if (!isLinked) {
                // time
            }

            if (showAppSelectionDialog) {
                AppUsageSelectionDialog(
                    viewModel = viewModel,
                    ruleId = usageRule.id,
                    onDismissRequest = { showAppSelectionDialog = false },
                    onConfirm = { selectedApps ->
                        viewModel.setRuleAppList(usageRule.id, selectedApps)
                        showAppSelectionDialog = false
                    }
                )
            }
        }
    }
}