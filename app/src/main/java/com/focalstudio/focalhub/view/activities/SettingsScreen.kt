package com.focalstudio.focalhub.view.activities

import android.view.GestureDetector
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.focalstudio.focalhub.view.viewModel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
data class SettingsScreen(val viewModel: SettingsViewModel) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val generalSettingsList by viewModel.generalSettingsList.collectAsState()
        val accountSettingsList by viewModel.accountSettingsList.collectAsState()
        //viewModel.setNavController(navController)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    //navigationIcon = { IconButton(onClick = { navController.navigateUp() }) {
                    navigationIcon = { IconButton(onClick = { navigator?.pop() }) {
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
    }




@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        fontSize = 26.sp,
        modifier = Modifier.padding(vertical = 18.dp, horizontal = 16.dp)
    )
}


@Composable
fun SettingItem(setting: String, description: String, onClick: () -> Unit, icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward) {
        //Settings Tile from https://github.com/alorma/Compose-Settings (MIT licensed)
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

