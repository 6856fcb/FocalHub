package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import com.focalstudio.focalhub.data.RuleRepositoryProvider
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.model.UsageRule
import com.focalstudio.focalhub.utils.log
import com.focalstudio.focalhub.utils.shouldNonLinkedUsageRuleBeCurrentlyActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class AppUsageViewModel(application: Application) : AndroidViewModel(application) {



    private val _appsList = mutableStateOf<List<App>>(emptyList())
    val appsList: State<List<App>> get() = _appsList

    private val _usageRules = MutableStateFlow<List<UsageRule>>(emptyList())
    val usageRules: StateFlow<List<UsageRule>> get() = _usageRules

    private val ruleRepository = RuleRepositoryProvider.getInstance(application)

    val navigator = LocalNavigator.current

    private var periodicRuleCheckJob: Job? = null

    init {
        loadApps()
        loadRules()
        loadUsageRules()
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

                _appsList.value = apps.filter { it.packageName != "com.focalstudio.focalhub" }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun setNavController(controller: NavController) {
        navController = controller
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addUsageRule() {
        val currentRules = _usageRules.value
        val newUsageRule = getDefaultUsageRuleData()

        _usageRules.value = currentRules + newUsageRule

        viewModelScope.launch {
            ruleRepository.addUsageRule(newUsageRule)
        }

        navController?.navigate("editUsageRule/${newUsageRule.id}")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultUsageRuleData(): UsageRule {
        val currentUsageRules = _usageRules.value
        val availableId = (currentUsageRules.maxOfOrNull { it.id } ?: 0) + 1

        val startOfDay = LocalDateTime.now().toLocalDate().atTime(8, 0)
        val endOfDay = startOfDay.plusDays(1).toLocalDate().atTime(0, 0)

        return UsageRule(
            id = availableId,
            name = "New Usage Rule$availableId",
            appList = emptyList(),
            ruleMode = 0,
            isLinkedToDisplayRule = false,
            linkedRuleId = 0,
            //onlyImportApps = true,
            isCurrentlyActive = false,
            isManuallyDisabled = false,
            restrictUsageTimePerApp = false,
            maxUsageDurationInSeconds= 0,
            useMaxSessionTimes = false,
            displaySessionDurationDialog = false,
            maxSessionDurationInSeconds = 0,
            isRestrictedUntilEndTime = false,
            timeWindowStartTime = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()),
            timeWindowEndTime = Date.from(endOfDay.minusHours(1).atZone(ZoneId.systemDefault()).toInstant()),
            isRecurring = false,
            weekdays = listOf(1,2,3,4)
        )
    }
    private fun loadRules() {
        viewModelScope.launch {
            _usageRules.value = ruleRepository.getUsageRules()

        }
    }
    init {
        loadUsageRules()
    }
    fun getUsageRuleById(ruleId: Int): UsageRule? {
        return _usageRules.value.find { it.id == ruleId }
    }

    fun getAllUsageRules(): List<UsageRule> {
        return _usageRules.value
    }

    private fun loadUsageRules() {
        viewModelScope.launch {
            _usageRules.value = ruleRepository.getUsageRules()
        }
    }

    fun addRule() {
        // TODO: Implement adding a new UsageRule
    }

    fun setRuleName(ruleId: Int, newName: String) {
        updateRuleField(ruleId) { it.copy(name = newName) }
    }

    fun setRuleAppList(ruleId: Int, newAppList: List<String>) {
        val filteredAppList = newAppList.filter { it.isNotEmpty() }
        updateRuleField(ruleId) { it.copy(appList = filteredAppList) }
    }


    fun setRuleMode(ruleId: Int, newMode: Int) {
        updateRuleField(ruleId) { it.copy(ruleMode = newMode) }
    }

    fun setIsLinkedToDisplayRule(ruleId: Int, isLinked: Boolean) {
        updateRuleField(ruleId) { it.copy(isLinkedToDisplayRule = isLinked) }
    }

    fun setLinkedRuleId(ruleId: Int, linkedId: Int) {
        updateRuleField(ruleId) { it.copy(linkedRuleId = linkedId) }
    }

    fun setIsCurrentlyActive(ruleId: Int, isActive: Boolean) {
        updateRuleField(ruleId) { it.copy(isCurrentlyActive = isActive) }
    }

    fun setIsManuallyDisabled(ruleId: Int, isDisabled: Boolean) {
        updateRuleField(ruleId) { it.copy(isManuallyDisabled = isDisabled) }
    }

    fun setRestrictUsageTimePerApp(ruleId: Int, restrict: Boolean) {
        updateRuleField(ruleId) { it.copy(restrictUsageTimePerApp = restrict) }
    }

    fun setMaxUsageDurationInSeconds(ruleId: Int, maxDuration: Int) {
        updateRuleField(ruleId) { it.copy(maxUsageDurationInSeconds = maxDuration) }
    }

    fun setRestrictUntilEndTime(ruleId: Int, restrict: Boolean) {
        updateRuleField(ruleId) { it.copy(isRestrictedUntilEndTime = restrict) }
        checkActiveRules()
    }

    fun setTimeWindowStartTime(ruleId: Int, startTime: Date) {
        updateRuleField(ruleId) { it.copy(timeWindowStartTime = startTime) }
        checkActiveRules()
    }

    fun setTimeWindowEndTime(ruleId: Int, endTime: Date) {
        updateRuleField(ruleId) { it.copy(timeWindowEndTime = endTime) }
        checkActiveRules()
    }
    fun deleteUsageRule(ruleId: Int) {
        viewModelScope.launch {
            // Delete the rule from the repository
            ruleRepository.deleteUsageRule(ruleId)

            // Update the _rules list after the rule is deleted
            val currentRules = _usageRules.value.toMutableList()
            val index = currentRules.indexOfFirst { it.id == ruleId }
            if (index != -1) {
                currentRules.removeAt(index)
                _usageRules.value = currentRules
            }

            // Navigate back
            navController?.popBackStack()
        }
    }


    private fun updateRuleField(ruleId: Int, update: (UsageRule) -> UsageRule) {
        val currentRules = _usageRules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = update(currentRules[index])
            _usageRules.value = currentRules
            viewModelScope.launch {
                ruleRepository.updateUsageRule(currentRules[index])
            }
        }
    }

    @Composable
    fun getIconForFirstAppInUsageRule(usageRule: UsageRule): Drawable {

        val app = appsList.value.find { usageRule.appList.first() == it.packageName }
        val context = LocalContext.current
        return app?.icon ?: AppCompatResources.getDrawable(context, android.R.drawable.sym_def_app_icon)!!
    }

    fun getAppNameForFirstAppInUsageRule(usageRule: UsageRule) : String {
        val app = appsList.value.find { usageRule.appList.first() == it.packageName }
        return app?.name ?: "Rule for App"
    }

    private suspend fun isAppInAnyUsageRule(app : App) : Boolean {

        val allUsageRules = ruleRepository.getUsageRules()

        var appFoundInARule = false
        if (allUsageRules.isNotEmpty()) {
            for (usageRule in allUsageRules) {
                if (usageRule.appList.contains(app.packageName)) {
                    appFoundInARule = true
                }
            }
        }
        return appFoundInARule
    }

    suspend fun getAppsThatAreNotInOtherUsageRules(usageRule: UsageRule) : List<App> {
        val appList = mutableListOf<App>()
        for (app in appsList.value) {
            log(app.packageName)
            if (usageRule.appList.contains(app.packageName)) {
                appList.add(app)
            } else if (!isAppInAnyUsageRule(app)) {
                appList.add(app)
            }
        }
        log(appList.size)
        return appList
    }

    fun setWeekdays(ruleId: Int, weekdays: List<Int>) {
        updateRuleField(ruleId) { it.copy(weekdays = weekdays) }
        checkActiveRules()
    }

    private fun checkActiveRules() {
        _usageRules.value.forEach { rule ->
            val isCurrentlyActive = shouldNonLinkedUsageRuleBeCurrentlyActive(rule)
            setIsCurrentlyActive(rule.id, isCurrentlyActive)
        }
    }

    fun setIsRecurring(ruleId: Int, rec: Boolean) {
        updateRuleField(ruleId) { it.copy(isRecurring = rec) }
        checkActiveRules()
    }


}
