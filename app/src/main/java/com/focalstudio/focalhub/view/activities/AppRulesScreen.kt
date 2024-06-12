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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.focalstudio.focalhub.view.viewModel.AppUsageViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRulesScreen(navController: NavController, viewModel: AppUsageViewModel) {

    viewModel.setNavController(navController)
    val usageRules by remember { viewModel.usageRules }.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Usage Rules") },
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
            items(usageRules) { usageRule ->

                val color: Color = if (usageRule.isManuallyDisabled || !usageRule.isCurrentlyActive) {
                    Color.LightGray
                } else {
                    Color.Black
                }

                SettingsMenuLink(
                    title = { Text(text = usageRule.name, color = color) },
                    subtitle = { Text(text = if (usageRule.isCurrentlyActive) "Active" else "Not Active", color = color) },
                    // eg put Active from 0:00 to 14:00 on Mon - Fri
                    modifier = Modifier,
                    enabled = true,
                    icon = {
                        if (usageRule.isCurrentlyActive) {
                            RadioButton(
                                selected = true,
                                onClick = {}
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
                        //Navigate to Edit Rule page passing the selected rule
                        navController.navigate("editUsageRule/${usageRule.id}")
                    },
                )
            }
        }
    }
}



