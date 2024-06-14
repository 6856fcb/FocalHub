package com.focalstudio.focalhub.view.activities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.List
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.focalstudio.focalhub.utils.log
import com.focalstudio.focalhub.view.viewModel.AppUsageViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.text.DateFormat


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
            FloatingActionButton(onClick = { viewModel.addUsageRule() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Rule")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(0.dp)
        ) {
            if (usageRules.isNotEmpty()) {
                items(usageRules) { usageRule ->

                    val color: Color =
                        if (usageRule.isManuallyDisabled || !usageRule.isCurrentlyActive) {
                            Color.LightGray
                        } else {
                            Color.Black
                        }

                    SettingsMenuLink(
                        title = {
                            Text(
                                text = if (usageRule.appList.size == 1) "Rule for " + viewModel.getAppNameForFirstAppInUsageRule(
                                    usageRule
                                ) else usageRule.name, color = color
                            )
                        },
                        subtitle = {
                            Text(
                                text = if (usageRule.isCurrentlyActive) "Active" else "Not Active",
                                color = color
                            )
                        },
                        modifier = Modifier,
                        enabled = true,
                        icon = {
                            if (usageRule.appList.size == 1) {
                                val painter: Painter = rememberDrawablePainter(
                                    viewModel.getIconForFirstAppInUsageRule(usageRule)
                                )
                                Image(
                                    painter = painter,
                                    contentDescription = "App Usage Rule",
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            color = Color.White,
                                            shape = CircleShape
                                        ) // For ripple effect
                                        .padding(0.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Outlined.List,
                                    contentDescription = null,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        },
                        onClick = {
                            navController.navigate("editUsageRule/${usageRule.id}")
                        },
                    )
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No App usage restrictions added yet",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 0.dp)
                        )
                    }
                }
            }
        }
    }
}




