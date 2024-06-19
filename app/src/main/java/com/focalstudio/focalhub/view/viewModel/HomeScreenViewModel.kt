package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.focalstudio.focalhub.data.RuleRepository
import com.focalstudio.focalhub.data.RuleRepositoryProvider
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.model.DisplayRule
import com.focalstudio.focalhub.data.model.UsageRule
import com.focalstudio.focalhub.utils.applyDisplayRules
import com.focalstudio.focalhub.utils.getAppUsageTimeInSeconds
import com.focalstudio.focalhub.utils.log
import com.focalstudio.focalhub.utils.shouldDisplayRuleBeCurrentlyActive
import com.focalstudio.focalhub.utils.shouldNonLinkedUsageRuleBeCurrentlyActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private var periodicRuleCheckJob: Job? = null

    private val _appsList = mutableStateOf<List<App>>(emptyList())
    val appsList: State<List<App>> get() = _appsList

    private val _allAppsList = mutableStateOf<List<App>>(emptyList())
    val allAppsList: State<List<App>> get() = _allAppsList

    private val _isVibrationEnabled = mutableStateOf(true)
    val isVibrationEnabled: State<Boolean> get() = _isVibrationEnabled

    private val ruleRepository: RuleRepository = RuleRepositoryProvider.getInstance(application.applicationContext)
    private val _rules = MutableStateFlow<List<DisplayRule>>(emptyList())
    private val rules: StateFlow<List<DisplayRule>> get() = _rules

    private val _usageRules = MutableStateFlow<List<UsageRule>>(emptyList())
    private val usageRules: StateFlow<List<UsageRule>> get() = _usageRules

    init {
        observeRuleChanges()
        loadRules()
        loadApps()
    }

    private fun observeRuleChanges() {
        viewModelScope.launch {
            ruleRepository.setRuleChangeListener {
                loadRules()
            }
        }
    }

    private fun loadRules() {
        viewModelScope.launch {
            _rules.value = ruleRepository.getDisplayRules()
            _usageRules.value = ruleRepository.getUsageRules()
            updateRulesState()
        }
    }

    private fun updateRulesState() {
        viewModelScope.launch {
            val updatedRules = _rules.value.map { rule ->
                val isActive = shouldDisplayRuleBeCurrentlyActive(rule)
                ruleRepository.updateDisplayRuleIsActive(rule.id, isActive)
                rule.copy(isActive = isActive)
            }
            _rules.value = updatedRules
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
                val filteredApps = applyDisplayRules(apps, rules.value)
                _appsList.value = filteredApps
                _allAppsList.value = apps
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        loadRules()
        loadApps()
        // periodic rule check coroutine when ViewModel resumed
        startPeriodicRuleCheck()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        // Cancel rule check coroutine when viewModel is paused
        periodicRuleCheckJob?.cancel()
    }

    private fun startPeriodicRuleCheck() {
        periodicRuleCheckJob = viewModelScope.launch {
            while (true) {
                loadApps()
                delay(60000) // Check every 60 seconds
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun appIconClicked(app: App, context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)

        tryAppLaunchAndApplyUsageRules(app, context)
    }

    private suspend fun tryAppLaunchAndApplyUsageRules(app: App, context: Context) {
        if (usageRules.value.isNotEmpty()) {
            var appFoundInARule = false

            for (usageRule in usageRules.value) {


                if (usageRule.appList.contains(app.packageName)) {
                    appFoundInARule = true

                    if (shouldNonLinkedUsageRuleBeCurrentlyActive(usageRule) && !usageRule.isLinkedToDisplayRule) {
                        // Apply Constraints -> app, usage rule
                    } else if (usageRule.isLinkedToDisplayRule) {
                        ruleRepository.getDisplayRuleById(usageRule.linkedRuleId)?.let {
                            shouldDisplayRuleBeCurrentlyActive(it)
                        }
                        // Apply Constraints -> app, usage rule
                    } else {
                        launchApp(context, app)
                    }
                }
            }
            if (!appFoundInARule) {
                launchApp(context, app)

            }
        } else {
            launchApp(context, app)
        }
    }

    private fun launchApp(context: Context, app: App) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        launchIntent?.let { context.startActivity(it) }
    }
}
