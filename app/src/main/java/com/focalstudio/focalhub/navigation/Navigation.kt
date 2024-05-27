package com.focalstudio.focalhub.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.focalstudio.focalhub.view.activities.EditRuleScreen
import com.focalstudio.focalhub.view.activities.SettingsScreen
import com.focalstudio.focalhub.view.activities.RulesManagerScreen
import com.focalstudio.focalhub.view.activities.HomeScreen
import com.focalstudio.focalhub.view.viewModel.HomeScreenViewModel
import com.focalstudio.focalhub.view.viewModel.RulesManagerViewModel
import com.focalstudio.focalhub.view.viewModel.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val homeScreenViewModel: HomeScreenViewModel = viewModel()
    val rulesManagerViewModel: RulesManagerViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            val apps = homeScreenViewModel.appsList.value
            HomeScreen(
                navController = navController,
                //apps = apps,
                context = navController.context,
                viewModel = homeScreenViewModel
            )
        }
        composable("settingsScreen") {
            SettingsScreen(navController, settingsViewModel)
        }
        composable("rulesScreen") {
            RulesManagerScreen(navController, rulesManagerViewModel)
        }
        composable(
            route = "editRule/{ruleId}",
            arguments = listOf(navArgument("ruleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getInt("ruleId")
            if (ruleId != null) {
                val rule = rulesManagerViewModel.getRuleById(ruleId)

                EditRuleScreen(navController, rulesManagerViewModel, rule, context = navController.context)
            } else {
                // Handle case where ruleId is null
                // You could navigate to an error screen or do something else
            }
        }
    }
}

