package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.focalstudio.focalhub.R

import com.focalstudio.focalhub.data.DisplayRuleRepositoryProvider
import com.focalstudio.focalhub.data.model.DisplayRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class RulesManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val ruleRepository = DisplayRuleRepositoryProvider.getInstance(application)
    private val _rules = MutableStateFlow<List<DisplayRule>>(emptyList())
    val rules: StateFlow<List<DisplayRule>> get() = _rules
    private var navController: NavController? = null

    init {
        loadRules()
    }

    fun setNavController(controller: NavController) {
        navController = controller
    }

    private fun loadRules() {
        viewModelScope.launch {
            _rules.value = ruleRepository.getRules()
        }
    }

    fun onRuleSelected(ruleId: Int) {
        updateRuleField(ruleId) { it.copy(isActive = true) }
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
        val currentDateTime = LocalDateTime.now()
        val dateTimeTwoHoursLater = currentDateTime.plusHours(2)
        val currentDateTimeAsDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val dateTimeTwoHoursLaterAsDate = Date.from(dateTimeTwoHoursLater.atZone(ZoneId.systemDefault()).toInstant())

        return DisplayRule(
            id = 999,
            name = "Rule",
            appList = emptyList(),
            isBlacklist = false,
            isActive = false,
            isRecurring = false,
            startTime = currentDateTimeAsDate,
            endTime = dateTimeTwoHoursLaterAsDate,
            weekdays = emptyList(),
            isDisabled = false,
            isEndTimeSet = false
        )
    }

    private fun updateRuleField(ruleId: Int, update: (DisplayRule) -> DisplayRule) {
        val currentRules = _rules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            currentRules[index] = update(currentRules[index])
            _rules.value = currentRules
            viewModelScope.launch {
                ruleRepository.updateRule(currentRules[index])
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addRule() {
        val currentRules = _rules.value
        val availableId = (currentRules.maxOfOrNull { it.id } ?: 0) + 1

        val startOfDay = LocalDateTime.now().toLocalDate().atTime(8, 0)
        val endOfDay = startOfDay.plusDays(1).toLocalDate().atTime(0, 0)

        val newRule = DisplayRule(
            id = availableId,
            name = "New Rule",
            appList = listOf("com.instagram.android"),
            isBlacklist = false,
            isActive = false,
            isRecurring = false,
            startTime = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()),
            endTime = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()),
            weekdays = emptyList(),
            isDisabled = false,
            isEndTimeSet = false
        )

        _rules.value = currentRules + newRule

        viewModelScope.launch {
            ruleRepository.addRule(newRule)
        }

        navController?.navigate("editRule/${newRule.id}")
    }


    fun updateRuleName(ruleId: Int, newName: String) {
        updateRuleField(ruleId) { it.copy(name = newName) }
    }

    fun updateRuleAppList(ruleId: Int, newAppList: List<String>) {
        updateRuleField(ruleId) { it.copy(appList = newAppList) }
    }

    fun updateRuleIsActive(ruleId: Int, newIsActive: Boolean) {
        updateRuleField(ruleId) { it.copy(isActive = newIsActive) }
    }

    fun updateRuleIsDisabled(ruleId: Int, newIsDisabled: Boolean) {
        updateRuleField(ruleId) { it.copy(isDisabled = newIsDisabled) }
    }

    fun updateRuleIsBlacklist(ruleId: Int, newIsBlacklist: Boolean) {
        updateRuleField(ruleId) { it.copy(isBlacklist = newIsBlacklist) }
    }

    fun updateRuleIsEndTimeSet(ruleId: Int, newIsEndTimeSet: Boolean) {
        updateRuleField(ruleId) { it.copy(isEndTimeSet = newIsEndTimeSet) }
    }

    fun updateRuleIsRecurring(ruleId: Int, newIsRecurring: Boolean) {


        updateRuleField(ruleId) { rule ->
            val updatedRule = rule.copy(isRecurring = newIsRecurring)
            val isActive = isRuleCurrentlyActive(updatedRule)
            updatedRule.copy(isActive = isActive)
        }
    }


    private fun isRuleCurrentlyActive(rule: DisplayRule): Boolean {
        val currentTime = Calendar.getInstance().time
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        val ruleStartTime = rule.startTime
        val ruleEndTime = rule.endTime
        val ruleDaysOfWeek = rule.weekdays

        return (currentTime.after(ruleStartTime) && currentTime.before(ruleEndTime) && ruleDaysOfWeek.contains(currentDayOfWeek))
    }


    fun updateRuleStartTime(ruleId: Int, startTime: Date) {
        updateRuleField(ruleId) { it.copy(startTime = startTime) }
    }

    fun updateRuleEndTime(ruleId: Int, endTime: Date) {
        updateRuleField(ruleId) { it.copy(endTime = endTime) }
    }

    fun updateRuleWeekdays(ruleId: Int, weekdays: List<Int>) {
        updateRuleField(ruleId) { it.copy(weekdays = weekdays) }
    }
}
