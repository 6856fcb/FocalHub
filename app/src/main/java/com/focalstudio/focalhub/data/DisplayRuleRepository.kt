package com.focalstudio.focalhub.data

import android.content.Context
import com.focalstudio.focalhub.data.model.DisplayRule
import java.util.Date
import java.util.concurrent.CopyOnWriteArrayList

interface DisplayRuleRepository {
    suspend fun getRules(): List<DisplayRule>
    fun addRule(rule: DisplayRule)
    fun updateRule(rule: DisplayRule)
    fun deleteRule(ruleId: Int)
    fun setRuleChangeListener(listener: () -> Unit)
    fun updateRuleName(ruleId: Int, newName: String)
    fun updateRuleAppList(ruleId: Int, newAppList: List<String>)
    fun updateRuleIsBlacklist(ruleId: Int, isBlacklist: Boolean)
    fun updateRuleIsActive(ruleId: Int, isActive: Boolean)
    fun updateRuleIsEndTimeSet(ruleId: Int, isEndTimeSet: Boolean)
    fun updateRuleIsDisabled(ruleId: Int, isDisabled: Boolean)
    fun updateRuleIsRecurring(ruleId: Int, isRecurring: Boolean)
    fun updateRuleStartTime(ruleId: Int, startTime: Date)
    fun updateRuleEndTime(ruleId: Int, endTime: Date)
    fun updateRuleWeekdays(ruleId: Int, weekdays: List<Int>)
}

object DisplayRuleRepositoryProvider {
    private var instance: DisplayRuleRepository? = null

    fun getInstance(context: Context): DisplayRuleRepository {
        return instance ?: synchronized(this) {
            instance ?: InMemoryDisplayRuleRepository().also { instance = it }
        }
    }
}

class InMemoryDisplayRuleRepository : DisplayRuleRepository {
    private val rules = CopyOnWriteArrayList<DisplayRule>()
    private var ruleChangeListener: (() -> Unit)? = null

    override suspend fun getRules(): List<DisplayRule> = rules.toList()

    override fun addRule(rule: DisplayRule) {
        rules.add(rule)
        notifyRuleChange()
    }

    override fun updateRule(rule: DisplayRule) {
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index != -1) {
            rules[index] = rule
            notifyRuleChange()
        }
    }

    override fun deleteRule(ruleId: Int) {
        val index = rules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            rules.removeAt(index)
            notifyRuleChange()
        }
    }

    override fun setRuleChangeListener(listener: () -> Unit) {
        ruleChangeListener = listener
    }

    override fun updateRuleName(ruleId: Int, newName: String) {
        updateRuleField(ruleId) { it.copy(name = newName) }
    }

    override fun updateRuleAppList(ruleId: Int, newAppList: List<String>) {
        updateRuleField(ruleId) { it.copy(appList = newAppList) }
    }

    override fun updateRuleIsBlacklist(ruleId: Int, isBlacklist: Boolean) {
        updateRuleField(ruleId) { it.copy(isBlacklist = isBlacklist) }
    }

    override fun updateRuleIsActive(ruleId: Int, isActive: Boolean) {
        updateRuleField(ruleId) { it.copy(isActive = isActive) }
    }

    override fun updateRuleIsDisabled(ruleId: Int, isDisabled: Boolean) {
        updateRuleField(ruleId) { it.copy(isDisabled = isDisabled) }
    }

    override fun updateRuleIsRecurring(ruleId: Int, isRecurring: Boolean) {
        updateRuleField(ruleId) { it.copy(isRecurring = isRecurring, isDisabled = !isRecurring) }
    }

    override fun updateRuleIsEndTimeSet(ruleId: Int, isEndTimeSet: Boolean) {
        updateRuleField(ruleId) { it.copy(isEndTimeSet = isEndTimeSet)}
    }
    override fun updateRuleStartTime(ruleId: Int, startTime: Date) {
        updateRuleField(ruleId) { it.copy(startTime = startTime) }
    }

    override fun updateRuleEndTime(ruleId: Int, endTime: Date) {
        updateRuleField(ruleId) { it.copy(endTime = endTime) }
    }

    override fun updateRuleWeekdays(ruleId: Int, weekdays: List<Int>) {
        updateRuleField(ruleId) { it.copy(weekdays = weekdays) }
    }

    private fun updateRuleField(ruleId: Int, update: (DisplayRule) -> DisplayRule) {
        val index = rules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            rules[index] = update(rules[index])
            notifyRuleChange()
        }
    }

    private fun notifyRuleChange() {
        ruleChangeListener?.invoke()
    }
}
