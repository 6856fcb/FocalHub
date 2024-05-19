package com.focalstudio.focalhub.view.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
//import com.focalstudio.focalhub.utils.theme.FocalHubTheme
import com.focalstudio.focalhub.utils.FocalHubTheme
import com.focalstudio.focalhub.view.composables.AppGrid
import com.focalstudio.focalhub.view.viewModel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val launcherViewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocalHubTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val apps by launcherViewModel.appsList.collectAsState()
                    AppGrid(apps)
                }
            }
        }
    }
}