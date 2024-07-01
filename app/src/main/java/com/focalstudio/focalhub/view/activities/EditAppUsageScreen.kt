package com.focalstudio.focalhub.view.activities

import AppUsageSelectionDialog
import NumberPickerDialog
import android.app.AlertDialog
import android.app.TimePickerDialog
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.focalstudio.focalhub.data.model.UsageRule
import com.focalstudio.focalhub.view.viewModel.AppUsageViewModel
import java.text.DateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
data class EditAppUsageScreen(
    val viewModel: AppUsageViewModel,
    val usageRule: UsageRule,
    val context: Context
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        var showNumberPicker by remember { mutableStateOf(false) }
        var showAppSelectionDialog by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf(usageRule.name) }
        var isCurrentlyActive by remember { mutableStateOf(usageRule.isCurrentlyActive) }
        var isRecurring by remember { mutableStateOf(usageRule.isRecurring) }
        var isDisabled by remember { mutableStateOf(usageRule.isManuallyDisabled) }
        var isEndTimeSet by remember { mutableStateOf(usageRule.isRestrictedUntilEndTime) }
        var selectedStartTime by remember { mutableStateOf(usageRule.timeWindowStartTime) }
        var selectedEndTime by remember { mutableStateOf(usageRule.timeWindowEndTime) }
        var selectedWeekdays by remember { mutableStateOf(usageRule.weekdays) }
        var restrictUsageTime by remember { mutableStateOf(usageRule.restrictUsageTimePerApp)}
        var maxUsageTimeInMinutes by remember { mutableIntStateOf(usageRule.maxUsageDurationInSeconds / 60) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Edit App Restrictions") },
                    navigationIcon = {
                        //IconButton(onClick = { navController.navigateUp() }) {
                        IconButton(onClick = { navigator?.pop() }) {
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
                    title = { Text(text = "Usage Restrictions active") },
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
                    title = { Text(text = "Choose one or more Apps") },
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
                    state = restrictUsageTime,
                    title = { Text(text = "Set usage limit") },
                    subtitle = {},
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    onCheckedChange = { newState: Boolean ->
                        restrictUsageTime = newState
                        viewModel.setRestrictUsageTimePerApp(ruleId = usageRule.id, newState)
                    },
                )
                if (restrictUsageTime) {
                    SettingsMenuLink(
                        title = { Text(text = "Set daily maximum usage duration") },
                        subtitle = { Text(text = "Currently set to: $maxUsageTimeInMinutes minutes") },
                        modifier = Modifier,
                        enabled = true,
                        icon = {},
                        action = {},
                        onClick = {
                            showNumberPicker = true
                        },
                    )
                }
                SettingsSwitch(
                    state = isRecurring,
                    title = { Text(text = "Schedule restrictions") },
                    subtitle = { Text(text = "App start disabled at selected time and day windows") },
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    onCheckedChange = { newState: Boolean ->
                        isRecurring = newState
                        viewModel.setIsRecurring(usageRule.id, isRecurring)
                    },
                )
                if (isRecurring) {
                    SettingsSwitch(
                        state = isDisabled,
                        title = { Text(text = "Disable Schedule temporarily") },
                        subtitle = {},
                        modifier = Modifier,
                        enabled = isRecurring,
                        icon = {},
                        onCheckedChange = { newState: Boolean ->
                            isDisabled = newState
                            viewModel.setIsManuallyDisabled(usageRule.id, isDisabled)
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
                                    viewModel.setWeekdays(usageRule.id, selectedWeekdays)
                                }
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        },
                    )
                    SettingsMenuLink(
                        title = { Text(text = "Choose start time") },
                        subtitle = { Text(text = "Restrictions start at ${DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedStartTime)}") },
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
                                    viewModel.setTimeWindowStartTime(usageRule.id, selectedStartTime)
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
                        title = { Text(text = "Forbid app start temporarily") },
                        subtitle = { Text(text = "Forbid App start until a certain time") },
                        modifier = Modifier,
                        enabled = true,
                        icon = {},
                        onCheckedChange = { newState: Boolean ->
                            isEndTimeSet = newState
                            viewModel.setRestrictUntilEndTime(usageRule.id, isEndTimeSet)
                        },
                    )
                }

                if (isEndTimeSet || isRecurring) {
                    SettingsMenuLink(
                        title = { Text(text = "Choose end time") },
                        subtitle = { Text(text = "Restrictions end at ${DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedEndTime)}") },
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
                                    viewModel.setTimeWindowEndTime(usageRule.id, selectedEndTime)
                                },
                                endHour,
                                endMinute,
                                true
                            )
                            endTimePickerDialog.show()
                        },
                    )
                }
                if (showNumberPicker) {


                    NumberPickerDialog(
                        range = 0..120, // Hardcoded Values, Replace with values from settings
                        pickerValue = maxUsageTimeInMinutes,
                        onValueChange = { maxUsageTimeInMinutes = it },
                        onConfirm = {
                            viewModel.setMaxUsageDurationInSeconds(ruleId = usageRule.id, maxDuration = maxUsageTimeInMinutes * 60)
                            showNumberPicker = false
                        },
                        onDismiss = { showNumberPicker = false }
                    )
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



}