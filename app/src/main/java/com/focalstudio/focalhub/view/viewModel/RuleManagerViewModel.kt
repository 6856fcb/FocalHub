package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.focalstudio.focalhub.data.DisplayRuleRepositoryProvider
import com.focalstudio.focalhub.data.model.DisplayRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class RulesManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val ruleRepository = DisplayRuleRepositoryProvider.getInstance(application)
    private val _rules = MutableStateFlow<List<DisplayRule>>(emptyList())
    val rules: StateFlow<List<DisplayRule>> get() = _rules
    private var navController: NavController? = null
    fun setNavController(controller: NavController) {
        navController = controller
    }
    init {
        loadRules()
    }

    private fun loadRules() {
        viewModelScope.launch {
            _rules.value = ruleRepository.getRules() // Assuming getRules() returns a list of rules
        }
    }

    fun onRuleSelected(ruleId: Int) {
        _rules.value = _rules.value.map { rule ->
            rule.copy(isActive = rule.id == ruleId)
        }
    }
    fun getRuleById(ruleId: Int): DisplayRule? {
        return _rules.value.find { it.id == ruleId }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRuleData(ruleId: Int): DisplayRule {
        return getRuleById(ruleId) ?: getDefaultRuleData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultRuleData(): DisplayRule {
        // empty rule for creating a new one
        val currentDateTime = LocalDateTime.now()
        val dateTimeTwoHoursLater = currentDateTime.plusHours(2)

        val currentDateTimeAsDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val dateTimeTwoHoursLaterAsDate = Date.from(dateTimeTwoHoursLater.atZone(ZoneId.systemDefault()).toInstant())
        return DisplayRule(appList = emptyList(), name = "Rule", endTime = dateTimeTwoHoursLaterAsDate, id = 2, isActive = false, isBlacklist = false, isRecurring = false, weekdays = emptyList(), startTime =  currentDateTimeAsDate)
    }
    fun updateRuleName(ruleId: Int, newName: String) {
        val currentRules = _rules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = currentRules[index].copy(name = newName)
            _rules.value = currentRules
        }
    }

    fun updateRuleAppList(ruleId: Int, newAppList: List<String>) {
        val currentRules = _rules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = currentRules[index].copy(appList = newAppList)
            _rules.value = currentRules
        }
    }

    fun updateRuleIsActive(ruleId: Int, newIsActive: Boolean) {
        val currentRules = _rules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = currentRules[index].copy(isActive = newIsActive)
            _rules.value = currentRules
        }
    }

    fun updateRuleIsBlacklist(ruleId: Int, newIsBlacklist: Boolean) {
        val currentRules = _rules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = currentRules[index].copy(isBlacklist = newIsBlacklist)
            _rules.value = currentRules
        }
    }

    fun updateRuleIsRecurring(ruleId: Int, newIsRecurring: Boolean) {
        val currentRules = _rules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = currentRules[index].copy(isRecurring = newIsRecurring)
            _rules.value = currentRules
        }
    }

}

