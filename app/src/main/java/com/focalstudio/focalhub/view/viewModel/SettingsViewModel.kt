package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _settingsList = MutableStateFlow<List<String>>(emptyList())
    val settingsList: StateFlow<List<String>> get() = _settingsList

    private val _accountSettingsList = MutableStateFlow<List<String>>(emptyList())
    val accountSettingsList: StateFlow<List<String>> get() = _accountSettingsList

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // dummy data
            _settingsList.value = listOf("Manage Rules", "Manage Apps", "Backup Settings")
            _accountSettingsList.value = listOf("Icons", "Theme", "Background", "Language")
        }
    }
}
