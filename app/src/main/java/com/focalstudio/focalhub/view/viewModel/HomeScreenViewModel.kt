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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.focalstudio.focalhub.data.RuleRepository
import com.focalstudio.focalhub.data.RuleRepositoryProvider
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.utils.applyDisplayRules
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeScreenViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private var periodicRuleCheckJob: Job? = null

    private val _appsList = mutableStateOf<List<App>>(emptyList())
    val appsList: State<List<App>> get() = _appsList

    private val _isVibrationEnabled = mutableStateOf(true)
    val isVibrationEnabled: State<Boolean> get() = _isVibrationEnabled

    private val ruleRepository: RuleRepository = RuleRepositoryProvider.getInstance(application.applicationContext)

    init {
        observeRuleChanges()
        loadApps()
        //startPeriodicRuleCheck()
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

                val filteredApps = applyDisplayRules(apps, ruleRepository)

                _appsList.value = filteredApps
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
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
    fun appIconClicked(app: App, context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)

        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        launchIntent?.let { context.startActivity(it) }
    }
}
