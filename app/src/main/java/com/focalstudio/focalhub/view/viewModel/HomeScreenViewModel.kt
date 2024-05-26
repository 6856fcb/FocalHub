package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.DisplayRuleRepository
import com.focalstudio.focalhub.data.DisplayRuleRepositoryProvider
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
            ruleRepository.getRules()
            loadApps()
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

                _appsList.value = filteredApps // Update the _appsList value here
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

        for (rule in displayRules) {
            // Check if the rule is within the specified time window and weekday
            val ruleStartTime = rule.startTime
            val ruleEndTime = rule.endTime
            val ruleDaysOfWeek = rule.weekdays
            if (!(currentTime.after(ruleStartTime) && currentTime.before(ruleEndTime) && ruleDaysOfWeek.contains(currentDayOfWeek))) {
                rule.isActive = true
            }

            if (rule.isActive) {
                for (app in apps) {
                    var isAllowed = true

                    // Blacklist active and app in List OR Whitelist active and app not present in List
                    if ((rule.isBlacklist && rule.appList.contains(app.packageName)) ||
                        (!rule.isBlacklist && !rule.appList.contains(app.packageName))
                    ) {
                        isAllowed = false
                    }

                    if (isAllowed) {
                        filteredApps.add(app)
                    }
                }
            }
        }
        return filteredApps.distinct()
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
