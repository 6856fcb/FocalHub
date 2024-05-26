package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _generalSettingsList = MutableStateFlow<List<SettingItem>>(emptyList())
    val generalSettingsList: StateFlow<List<SettingItem>> get() = _generalSettingsList

    private val _accountSettingsList = MutableStateFlow<List<SettingItem>>(emptyList())
    val accountSettingsList: StateFlow<List<SettingItem>> get() = _accountSettingsList

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Load general settings
            _generalSettingsList.value = listOf(
                SettingItem("Manage Rules", "Configure and manage rules") { handleGeneralSettingClick("Manage Rules") },
                SettingItem("Manage Apps", "Manage installed applications") { handleGeneralSettingClick("Manage Apps") },
                SettingItem("Backup Settings", "Backup your settings data") { handleGeneralSettingClick("Backup Settings") }
            )
            // Load account settings
            _accountSettingsList.value = listOf(
                SettingItem("Icons", "Customize app icons") { handleAccountSettingClick("Icons") },
                SettingItem("Theme", "Change application theme") { handleAccountSettingClick("Theme") },
                SettingItem("Background", "Set application background") { handleAccountSettingClick("Background") },
                SettingItem("Language", "Select application language") { handleAccountSettingClick("Language") }
            )
        }
    }

    private fun handleGeneralSettingClick(setting: String) {
        // Handle general setting click
    }

    private fun handleAccountSettingClick(setting: String) {
        // Handle account setting click
    }
}

data class SettingItem(val name: String, val description: String, val onClick: () -> Unit)
