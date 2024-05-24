package com.focalstudio.focalhub.view.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.focalstudio.focalhub.navigation.Navigation
import com.focalstudio.focalhub.utils.FocalHubTheme
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel
import com.focalstudio.focalhub.view.viewModel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val homeScreenViewModel: HomeScreenViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocalHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
