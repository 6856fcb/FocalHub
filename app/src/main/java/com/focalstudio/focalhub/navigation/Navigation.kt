package com.focalstudio.focalhub.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.focalstudio.focalhub.view.activities.CreateRuleScreen
import com.focalstudio.focalhub.view.activities.SettingsScreen
import com.focalstudio.focalhub.view.activities.ShowRulesScreen
import com.focalstudio.focalhub.view.activities.HomeScreen
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel
import com.focalstudio.focalhub.view.viewModel.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val homeScreenViewModel: HomeScreenViewModel = viewModel()

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            val apps = homeScreenViewModel.appsList.collectAsState().value
            HomeScreen(
                navController = navController,
                apps = apps,
                context = navController.context,
                viewModel = homeScreenViewModel

            )
        }
        composable("settingsScreen") {
            SettingsScreen(navController, settingsViewModel)
        }
        composable("rulesScreen") {
            ShowRulesScreen(navController)
        }
        composable("createRuleScreen") {
            CreateRuleScreen(navController)
        }
    }
}
