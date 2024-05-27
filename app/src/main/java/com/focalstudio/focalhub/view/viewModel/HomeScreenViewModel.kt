package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focalstudio.focalhub.data.DisplayRuleRepository
import com.focalstudio.focalhub.data.DisplayRuleRepositoryProvider
import com.focalstudio.focalhub.data.model.App
import kotlinx.coroutines.launch
import java.util.*

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val _appsList = mutableStateOf<List<App>>(emptyList())
    val appsList: State<List<App>> get() = _appsList

    private val _isVibrationEnabled = mutableStateOf(true)
    val isVibrationEnabled: State<Boolean> get() = _isVibrationEnabled

    private val ruleRepository: DisplayRuleRepository = DisplayRuleRepositoryProvider.getInstance(application.applicationContext)

    init {
        observeRuleChanges()
        loadApps()
    }

    private fun observeRuleChanges() {
        viewModelScope.launch {
            ruleRepository.setRuleChangeListener {
                loadApps()
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            try {
                val pm: PackageManager = getApplication<Application>().packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                val apps = pm.queryIntentActivities(mainIntent, 0)
                    .map { resolveInfo ->
                        App(
                            name = resolveInfo.loadLabel(pm).toString(),
                            packageName = resolveInfo.activityInfo.packageName,
                            icon = resolveInfo.loadIcon(pm)
                        )
                    }
                val filteredApps = applyDisplayRules(apps)

                _appsList.value = filteredApps
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun applyDisplayRules(apps: List<App>): List<App> {
        val displayRules = ruleRepository.getRules()
        val currentTime = Calendar.getInstance().time
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val filteredApps = mutableListOf<App>()

        if (displayRules.isEmpty()) {
            return apps
        }

        // Check recurring rules and update their active states
        for (rule in displayRules) {
            if (rule.isRecurring && !rule.isDisabled) {
                rule.isActive = currentTime.after(rule.startTime) &&
                        currentTime.before(rule.endTime) &&
                        rule.weekdays.contains(currentDayOfWeek)
            }
            if (!rule.isRecurring && rule.isActive && rule.isEndTimeSet) {
                rule.isActive = currentTime.before(rule.endTime)
            }
        }


        // Find active blacklisted and whitelisted apps
        val blacklistedApps = mutableSetOf<String>()
        val whitelistedApps = mutableSetOf<String>()
        for (rule in displayRules) {
            if (rule.isActive) {
                if (!rule.isRecurring && !currentTime.before(rule.endTime) && rule.isEndTimeSet) {
                    rule.isActive = false
                    continue
                }
                if (rule.isBlacklist) {
                    blacklistedApps.addAll(rule.appList)
                } else {
                    whitelistedApps.addAll(rule.appList)
                }
            }
        }

        // Apply priorities
        if (blacklistedApps.isNotEmpty()) {
            // Blacklist exists, prioritize it over whitelist
            for (app in apps) {
                if (whitelistedApps.contains(app.packageName)) {
                    // Allow whitelisted apps
                    filteredApps.add(app)
                } else if (blacklistedApps.contains(app.packageName)) {
                    // Block blacklisted apps
                    continue
                } else if (whitelistedApps.isEmpty()){filteredApps.add(app)}
            }
        } else {
            // No blacklisted apps, allow only whitelisted apps
            for (app in apps) {
                if (whitelistedApps.contains(app.packageName)) {
                    // Allow whitelisted apps
                    filteredApps.add(app)
                }
            }
        }

        return filteredApps.ifEmpty { apps }

    }





    @RequiresApi(Build.VERSION_CODES.O)
    fun appIconClicked(app: App, context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)

        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        launchIntent?.let { context.startActivity(it) }
    }
}
