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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.focalstudio.focalhub.filtering.filterFreeTime

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val _appsList = MutableStateFlow<List<App>>(emptyList())
    val appsList: StateFlow<List<App>> get() = _appsList.asStateFlow()

    // Dummy setting for vibration
    private val _isVibrationEnabled = MutableStateFlow(true)
    val isVibrationEnabled: StateFlow<Boolean> get() = _isVibrationEnabled.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {

            // only for testing purpose
            val test = true;



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
            val filteredApps = apps.filter { app ->
                val filterBy: String = "";
                filterFreeTime.contains(app.packageName)

            }

            // instead of "test", we should implement a way where the user can set this parameter and accordingly
            // set the filtered apps
            _appsList.update {

                // set to "test" instead of "!test" to only see the filtered apps
                if (!test) {
                    filteredApps
                } else {
                    apps
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun provideFeedback() {
        val context = getApplication<Application>()
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }
}
