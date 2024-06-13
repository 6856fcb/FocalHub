package com.focalstudio.focalhub.view.activities
import AppSelectionDialog
import RulesAppSelectionDialog
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.focalstudio.focalhub.view.viewModel.RulesManagerViewModel
import androidx.compose.runtime.*
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.focalstudio.focalhub.data.model.DisplayRule
import java.text.DateFormat
import java.util.Calendar

fun getSelectedWeekdaysString(selectedWeekdays: List<Int>): String {
    val daysOfWeek = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val selectedDays = selectedWeekdays.map { daysOfWeek[it - 1] }
    return selectedDays.joinToString(", ")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRuleScreen(
    navController: NavController,
    viewModel: RulesManagerViewModel,
    rule: DisplayRule,
    context: Context
) {
    var name by remember { mutableStateOf(rule.name) }
    var isActive by remember { mutableStateOf(rule.isActive) }
    var isBlacklist by remember { mutableStateOf(rule.isBlacklist) }
    var isRecurring by remember { mutableStateOf(rule.isRecurring) }
    var isDisabled by remember { mutableStateOf(rule.isDisabled) }
    var isEndTimeSet by remember { mutableStateOf(rule.isEndTimeSet) }
    var selectedStartTime by remember { mutableStateOf(rule.startTime) }
    var selectedEndTime by remember { mutableStateOf(rule.endTime) }
    var selectedWeekdays by remember { mutableStateOf(rule.weekdays) }
    var showAppSelectionDialog by remember { mutableStateOf(false) }

    val apps by remember { mutableStateOf(viewModel.appsList) }


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
            FloatingActionButton(onClick = { viewModel.deleteRule(rule.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Rule")
            }
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
                enabled = !isRecurring,
                icon = {},
                onCheckedChange = { newState: Boolean ->
                    isActive = newState
                    viewModel.updateRuleIsActive(rule.id, isActive)
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
            SettingsSwitch(
                state = isBlacklist,
                title = { Text(text = "Blacklist Mode") },
                subtitle = { Text(text = "Exclude selected apps") },
                modifier = Modifier,
                enabled = true,
                icon = {},
                onCheckedChange = { newState: Boolean ->
                    isBlacklist = newState
                    viewModel.updateRuleIsBlacklist(rule.id, isBlacklist)
                },//
            )
            SettingsSwitch(
                state = isRecurring,
                title = { Text(text = "Recurring Rule") },
                subtitle = { Text(text = "Active at the selected time and days") },
                modifier = Modifier,
                enabled = true,
                icon = {},
                onCheckedChange = { newState: Boolean ->
                    isRecurring = newState
                    viewModel.updateRuleIsRecurring(rule.id, isRecurring)
                },
            )
            if (isRecurring) {
                SettingsSwitch(
                    state = isDisabled,
                    title = { Text(text = "Disable Rule") },
                    subtitle = {},
                    modifier = Modifier,
                    enabled = isRecurring,
                    icon = {},
                    onCheckedChange = { newState: Boolean ->
                        isDisabled = newState
                        viewModel.updateRuleIsDisabled(rule.id, isDisabled)
                    },
                )

                SettingsMenuLink(
                    title = { Text(text = "Select Weekdays") },
                    subtitle = { Text(text = "Active on ${getSelectedWeekdaysString(selectedWeekdays)}") },
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    action = {},
                    onClick = {
                        val daysOfWeek = arrayOf(
                            "Sunday", "Monday", "Tuesday", "Wednesday",
                            "Thursday", "Friday", "Saturday"
                        )
                        val selectedItems = BooleanArray(daysOfWeek.size) { selectedWeekdays.contains(it + 1) }

                        AlertDialog.Builder(context)
                            .setTitle("Select Weekdays")
                            .setMultiChoiceItems(daysOfWeek, selectedItems) { _, which, isChecked ->
                                if (isChecked) {
                                    selectedWeekdays = selectedWeekdays + (which + 1)
                                } else {
                                    selectedWeekdays = selectedWeekdays - (which + 1)
                                }
                                viewModel.updateRuleWeekdays(rule.id, selectedWeekdays)
                            }
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    },
                )
                SettingsMenuLink(
                    title = { Text(text = "Choose start time") },
                    subtitle = { Text(text = "Rule starts at ${DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedStartTime)}") },
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    action = {},
                    onClick = {
                        val startCalendar = Calendar.getInstance()
                        startCalendar.time = selectedStartTime
                        val startHour = startCalendar.get(Calendar.HOUR_OF_DAY)
                        val startMinute = startCalendar.get(Calendar.MINUTE)

                        val startTimePickerDialog = TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                val updatedStartTime = Calendar.getInstance()
                                updatedStartTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                                updatedStartTime.set(Calendar.MINUTE, selectedMinute)
                                selectedStartTime = updatedStartTime.time
                                viewModel.updateRuleStartTime(rule.id, selectedStartTime)
                            },
                            startHour,
                            startMinute,
                            true
                        )
                        startTimePickerDialog.show()
                    },
                )
            } else {
                SettingsSwitch(
                    state = isEndTimeSet,
                    title = { Text(text = "Set end time") },
                    subtitle = { Text(text = "Disable Rule at specific time") },
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    onCheckedChange = { newState: Boolean ->
                        isEndTimeSet = newState
                        viewModel.updateRuleIsEndTimeSet(rule.id, isEndTimeSet)
                    },
                )
            }

            if (isEndTimeSet || isRecurring) {
                SettingsMenuLink(
                    title = { Text(text = "Choose end time") },
                    subtitle = { Text(text = "Rule ends at ${DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedEndTime)}") },
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    action = {},
                    onClick = {
                        val endCalendar = Calendar.getInstance()
                        endCalendar.time = selectedEndTime
                        val endHour = endCalendar.get(Calendar.HOUR_OF_DAY)
                        val endMinute = endCalendar.get(Calendar.MINUTE)

                        val endTimePickerDialog = TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                val updatedEndTime = Calendar.getInstance()
                                updatedEndTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                                updatedEndTime.set(Calendar.MINUTE, selectedMinute)
                                selectedEndTime = updatedEndTime.time
                                viewModel.updateRuleEndTime(rule.id, selectedEndTime)
                            },
                            endHour,
                            endMinute,
                            true
                        )
                        endTimePickerDialog.show()
                    },
                )
            }

            if (showAppSelectionDialog) {
                RulesAppSelectionDialog(
                    viewModel = viewModel,
                    ruleId = rule.id,
                    onDismissRequest = { showAppSelectionDialog = false },
                    onConfirm = { selectedApps ->
                        viewModel.updateRuleAppList(rule.id, selectedApps)
                        showAppSelectionDialog = false
                    }
                )
            }
        }
    }
}



