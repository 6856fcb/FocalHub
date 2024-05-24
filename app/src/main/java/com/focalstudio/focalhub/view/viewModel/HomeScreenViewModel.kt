package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.DisplayRuleRepository
import com.focalstudio.focalhub.data.DisplayRuleRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val _appsList = MutableStateFlow<List<App>>(emptyList())
    val appsList: StateFlow<List<App>> get() = _appsList.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(true)
    val isVibrationEnabled: StateFlow<Boolean> get() = _isVibrationEnabled.asStateFlow()

    private val ruleRepository: DisplayRuleRepository = DisplayRuleRepositoryProvider.getInstance(application.applicationContext)

    init {
        loadApps()
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

                _appsList.update {
                    filteredApps
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun applyDisplayRules(apps: List<App>): List<App> {
        val displayRules = ruleRepository.getRules()
        val currentTime = Calendar.getInstance().time
        val filteredApps = mutableListOf<App>()

        for (app in apps) {
            var isAllowed = true
            for (rule in displayRules) {
                if (!rule.isActive) {
                    continue
                }

                if ((rule.isBlacklist && rule.appList.contains(app.packageName)) || (!rule.isBlacklist && !rule.appList.contains(app.packageName))) {
                    isAllowed = false
                    break
                }
                //TODO More rule checking logic
            }

            if (isAllowed) {
                filteredApps.add(app)
            }
        }
        return filteredApps
    }


    private fun isTimeWithinWindow(currentTime: Date, startTime: Date, endTime: Date): Boolean {
        return currentTime.after(startTime) && currentTime.before(endTime)
    }

    private fun isDayMatch(currentTime: Date, weekdays: List<Int>): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = currentTime
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekdays.contains(currentDay)
    }



    private fun isRecurring(currentTime: Date, startTime: Date, endTime: Date, weekdays: List<Int>): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = currentTime
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekdays.contains(currentDay) && isTimeWithinWindow(currentTime, startTime, endTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun AppIconClicked(app: App, context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)

        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        launchIntent?.let { context.startActivity(it) }
    }
}
