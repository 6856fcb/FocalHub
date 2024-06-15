package com.focalstudio.focalhub.view.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.focalstudio.focalhub.view.viewModel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val generalSettingsList by viewModel.generalSettingsList.collectAsState()
    val accountSettingsList by viewModel.accountSettingsList.collectAsState()
    viewModel.setNavController(navController)
    var isGeneralSettingsExpanded by remember { mutableStateOf(false) }
    var isAppearanceSettingsExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings",
                    fontSize = 25.sp) },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", Modifier.size(35.dp))
                }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(20.dp)
        ) {
            item {
                SettingsSection(title = "General Settings", onClick = { isGeneralSettingsExpanded = !isGeneralSettingsExpanded })
            }
            if (isGeneralSettingsExpanded) {
                items(generalSettingsList) { settingItem ->
                    SettingItem(setting = settingItem.name, description = settingItem.description, onClick = settingItem.onClick)
                }
            }
            item {
                SettingsSection(title = "Appearance", onClick = { isAppearanceSettingsExpanded = !isAppearanceSettingsExpanded })
            }
            if (isAppearanceSettingsExpanded) {
                items(accountSettingsList) { settingItem ->
                    SettingItem(setting = settingItem.name, description = settingItem.description, onClick = settingItem.onClick)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(vertical = 18.dp, horizontal = 16.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun SettingItem(setting: String, description: String, onClick: () -> Unit, icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward) {
    // Settings Tile from https://github.com/alorma/Compose-Settings (MIT licensed)
    SettingsMenuLink(
        title = { Text(text = setting) },
        subtitle = { Text(text = description, color = Color.Gray) },
        modifier = Modifier,
        enabled = true,
        icon = {},
        action = {},
        onClick = onClick,
    )
}



