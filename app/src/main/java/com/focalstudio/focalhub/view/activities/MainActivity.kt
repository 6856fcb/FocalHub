package com.focalstudio.focalhub.view.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.focalstudio.focalhub.navigation.Navigation
import com.focalstudio.focalhub.ui.FocalHubTheme
import com.focalstudio.focalhub.utils.AppUsageMonitoringService
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HomeScreenViewModel
    private val TAG = "MainActivityLifecycle"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        viewModel = ViewModelProvider(this).get(HomeScreenViewModel::class.java)

        setContent {
            FocalHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(lifecycleOwner = this@MainActivity)
                }
            }
        }

        // Start the background service
        startService(Intent(this, AppUsageMonitoringService::class.java))
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        viewModel.onPause()
    }
}
