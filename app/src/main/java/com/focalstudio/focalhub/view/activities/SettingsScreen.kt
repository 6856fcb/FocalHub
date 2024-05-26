package com.focalstudio.focalhub.view.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.focalstudio.focalhub.view.viewModel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val generalSettingsList by viewModel.generalSettingsList.collectAsState()
    val accountSettingsList by viewModel.accountSettingsList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FocalHub Settings") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(0.dp)
        ) {
            item {
                SettingsSection(title = "General Settings")
            }
            items(generalSettingsList) { settingItem ->
                SettingItem(setting = settingItem.name, description = settingItem.description, onClick = settingItem.onClick)
            }
            item {
                SettingsSection(title = "Appearance")
            }
            items(accountSettingsList) { settingItem ->
                SettingItem(setting = settingItem.name, description = settingItem.description, onClick = settingItem.onClick)
            }
        }
    }
}


@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        modifier = Modifier.padding(vertical = 18.dp, horizontal = 16.dp)
    )
}


@Composable
fun SettingItem(setting: String, description: String, onClick: () -> Unit, icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        /*Icon(
            imageVector = icon, // Default icon, can be overridden
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )*/
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp, start = 24.dp)
        ) {
            Text(
                text = setting,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 0.dp)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

    }
}

