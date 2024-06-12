package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.navigation.NavController


class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _generalSettingsList = MutableStateFlow<List<SettingItem>>(emptyList())
    val generalSettingsList: StateFlow<List<SettingItem>> get() = _generalSettingsList

    private val _accountSettingsList = MutableStateFlow<List<SettingItem>>(emptyList())
    val accountSettingsList: StateFlow<List<SettingItem>> get() = _accountSettingsList


    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        navController = controller
    }
    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Load general settings
            _generalSettingsList.value = listOf(
                SettingItem("Manage Rules", "Configure and manage rules") { handleSettingClick("Manage Rules") },
                SettingItem("Manage Apps", "Manage installed applications") { handleSettingClick("Manage Apps") },
                SettingItem("Backup Settings", "Backup your settings data") { handleSettingClick("Backup Settings") }
            )
            // Load account settings
            _accountSettingsList.value = listOf(
                SettingItem("Icons", "Customize app icons") { handleSettingClick("Icons") },
                SettingItem("Theme", "Change application theme") { handleSettingClick("Theme") },
                SettingItem("Background", "Set application background") { handleSettingClick("Background") },
                SettingItem("Language", "Select application language") { handleSettingClick("Language") }
            )
        }
    }

    private fun handleSettingClick(setting: String) {
        if (setting == "Manage Rules" && navController != null) {
            navController?.navigate("rulesScreen")
        }
        if (setting == "Manage Apps" && navController != null) {
            navController?.navigate("usageRulesScreen")
        }
    }
}

data class SettingItem(val name: String, val description: String, val onClick: () -> Unit)
