package com.focalstudio.focalhub.view.activities

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
import androidx.compose.material3.RadioButton
import com.focalstudio.focalhub.data.model.DisplayRule


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesManagerScreen(navController: NavController, viewModel: RulesManagerViewModel) {

    viewModel.setNavController(navController)
    val rules by viewModel.rules.collectAsState()

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
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(0.dp)
        ) {
            items(rules) { rule ->
                val radioState = remember { mutableStateOf(rule.isActive) }
                var isActive = rule.isActive
                SettingsMenuLink(
                    title = { Text(text = rule.name) },
                    subtitle = {},
                    modifier = Modifier,
                    enabled = true,
                    icon = {},
                    action = {
                        RadioButton(
                            selected = isActive,
                            onClick = {
                                isActive = !isActive
                                viewModel.updateRuleIsActive(rule.id, isActive)
                            }
                        )
                    },
                    onClick = {
                        // Navigate to Edit Rule page passing the selected rule
                        navController.navigate("editRule/${rule.id}")
                    },
                )
            }
        }
    }
}


