package com.focalstudio.focalhub.view.activities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.focalstudio.focalhub.view.viewModel.RulesManagerViewModel
import java.text.DateFormat


fun getSelectedShortWeekdaysString(selectedWeekdays: List<Int>): String {
    val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val selectedDays = selectedWeekdays.map { daysOfWeek[it - 1] }
    return selectedDays.joinToString(", ")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesManagerScreen(navController: NavController, viewModel: RulesManagerViewModel) {

    viewModel.setNavController(navController)
    val rules by remember { viewModel.rules }.collectAsState()
    //val rules by viewModel.rules.collectAsState()
    val noActiveRules = rules.all { !it.isActive }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Rules") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addRule() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Rule")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(0.dp)
        ) {
            item {
                val color: Color = if (!noActiveRules) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
                SettingsMenuLink(
                    title = { Text(text = "Default Rule", color = MaterialTheme.colorScheme.secondary) },
                    subtitle = { Text(text = "Show all Apps", color = MaterialTheme.colorScheme.secondary) },
                    modifier = Modifier,
                    enabled = noActiveRules,
                    icon = {},
                    action = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp)
                        )
                    },
                    onClick = {}
                )
            }
            items(rules) { rule ->
                val selectedStartTime by remember { mutableStateOf(rule.startTime) }
                val selectedEndTime by remember { mutableStateOf(rule.endTime) }
                val selectedWeekdays by remember { mutableStateOf(rule.weekdays) }
                var isActive = rule.isActive
                val isEndTimeSet by remember { mutableStateOf(rule.isEndTimeSet)}

                val color: Color = if ((rule.isDisabled && rule.isRecurring) || !isActive) {
                    Color.LightGray
                } else {
                    Color.Black
                }

                SettingsMenuLink(
                    title = { Text(text = rule.name, color = color) },
                    subtitle = {
                        if (rule.isRecurring) {
                            Text(
                                text = "From ${
                                    DateFormat.getTimeInstance(DateFormat.SHORT)
                                        .format(selectedStartTime)
                                } - ${
                                    DateFormat.getTimeInstance(DateFormat.SHORT)
                                        .format(selectedEndTime)
                                } on ${getSelectedShortWeekdaysString(selectedWeekdays)}",
                                color = color
                            )
                        }
                        if (isActive) {
                            Text(
                                text = "Active ${
                                    if (isEndTimeSet) "until ${
                                        DateFormat.getTimeInstance(DateFormat.SHORT)
                                            .format(selectedEndTime)
                                    }"
                                    else
                                        ""
                                }", color = color
                            )
                        } else if (isEndTimeSet){
                            Text(text = "Deactivated at  ${
                                DateFormat.getTimeInstance(DateFormat.SHORT)
                                    .format(selectedEndTime)
                            }", color = color)
                        } else {
                            Text(text = "Not Active", color = color)
                        }
                    },
                    //eg put Active from 0:00 to 14:00 on Mon - Fri
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    action = {
                        if (!rule.isRecurring) {
                            RadioButton(
                                selected = isActive,
                                onClick = {
                                    isActive = !isActive
                                    viewModel.updateRuleIsActive(rule.id, isActive)
                                }
                            )
                        } else {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp)
                            )

                        }
                    },
                    onClick = {
                        //viewModel.stopPeriodicRuleCheck()
                        // Navigate to Edit Rule page passing the selected rule
                        navController.navigate("editRule/${rule.id}")
                    },
                )
            }

        }
    }
}

