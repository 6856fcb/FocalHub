package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.focalstudio.focalhub.data.RuleRepositoryProvider
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.model.UsageRule
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class AppUsageViewModel(application: Application) : AndroidViewModel(application) {

    private val _appsList = mutableStateOf<List<App>>(emptyList())
    val appsList: State<List<App>> get() = _appsList

    private val _usageRules = MutableStateFlow<List<UsageRule>>(emptyList())
    val usageRules: StateFlow<List<UsageRule>> get() = _usageRules

    private val ruleRepository = RuleRepositoryProvider.getInstance(application)
    private var navController: NavController? = null

    private var periodicRuleCheckJob: Job? = null

    init {
        loadUsageRules()

    }

    fun setNavController(controller: NavController) {
        navController = controller
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
        updateRuleField(ruleId) { it.copy(appList = newAppList) }
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

    fun setOnlyImportApps(ruleId: Int, onlyImport: Boolean) {
        updateRuleField(ruleId) { it.copy(onlyImportApps = onlyImport) }
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

    fun setUseMaxSessionTimes(ruleId: Int, useMax: Boolean) {
        updateRuleField(ruleId) { it.copy(useMaxSessionTimes = useMax) }
    }

    fun setDisplaySessionDurationDialog(ruleId: Int, display: Boolean) {
        updateRuleField(ruleId) { it.copy(displaySessionDurationDialog = display) }
    }

    fun setMaxSessionDurationInSeconds(ruleId: Int, maxDuration: Int) {
        updateRuleField(ruleId) { it.copy(maxSessionDurationInSeconds = maxDuration) }
    }

    fun setRestrictUntilEndTime(ruleId: Int, restrict: Boolean) {
        updateRuleField(ruleId) { it.copy(restrictUntilEndTime = restrict) }
    }

    fun setTimeWindowStartTime(ruleId: Int, startTime: Date) {
        updateRuleField(ruleId) { it.copy(timeWindowStartTime = startTime) }
    }

    fun setTimeWindowEndTime(ruleId: Int, endTime: Date) {
        updateRuleField(ruleId) { it.copy(timeWindowEndTime = endTime) }
    }

    private fun updateRuleField(ruleId: Int, update: (UsageRule) -> UsageRule) {
        _usageRules.value = _usageRules.value.map {
            if (it.id == ruleId) update(it) else it
        }
    }
}
