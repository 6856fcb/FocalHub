package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import com.focalstudio.focalhub.data.model.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val _appsList = MutableStateFlow<List<App>>(emptyList())
    val appsList: StateFlow<List<App>> get() = _appsList.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val pm: PackageManager = getApplication<Application>().packageManager
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .map { appInfo ->
                    App(
                        name = appInfo.loadLabel(pm).toString(),
                        packageName = appInfo.packageName,
                        icon = appInfo.loadIcon(pm)
                    )
                }
            _appsList.update { apps }
        }
    }
}