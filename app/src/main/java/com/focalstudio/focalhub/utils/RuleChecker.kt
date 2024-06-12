package com.focalstudio.focalhub.utils

import com.focalstudio.focalhub.data.RuleRepository
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.model.DisplayRule
import java.util.Calendar

suspend fun applyDisplayRules(apps: List<App>, ruleRepository: RuleRepository): List<App> {
    val displayRules = ruleRepository.getRules()

    val filteredApps = mutableListOf<App>()

    if (displayRules.isEmpty()) {
        return apps
    }

    for (rule in displayRules) {

        val isActive = isRuleActive(rule)

        ruleRepository.updateRuleIsActive(rule.id, isActive)
        rule.isActive = isActive
    }

    // Find active blacklisted and whitelisted apps
    val blacklistedApps = mutableSetOf<String>()
    val whitelistedApps = mutableSetOf<String>()
    for (rule in displayRules) {
        if (rule.isActive) {
            if (rule.isBlacklist) {
                blacklistedApps.addAll(rule.appList)
            } else {
                whitelistedApps.addAll(rule.appList)
            }
        }
    }

    // Apply priorities
    if (blacklistedApps.isNotEmpty()) {
        for (app in apps) {
            if (blacklistedApps.contains(app.packageName)) {
                // Block blacklisted apps
                continue

            } else if (whitelistedApps.contains(app.packageName)) {
                // Allow whitelisted apps
                filteredApps.add(app)

            } else if (whitelistedApps.isEmpty())
            // No whitelisted items but item not in blacklist either
            {filteredApps.add(app)}
        }
    } else {
        // No blacklisted apps, allow only whitelisted apps
        for (app in apps) {
            if (whitelistedApps.contains(app.packageName)) {
                // Allow whitelisted apps
                filteredApps.add(app)
            }
        }
    }

    return filteredApps.ifEmpty { apps }

}

fun isRuleActive (rule: DisplayRule): Boolean {
    var result = rule.isActive
    val currentTime = Calendar.getInstance().time
    val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    if (rule.isRecurring && !rule.isDisabled) {
        result = currentTime.after(rule.startTime) &&
                currentTime.before(rule.endTime) &&
                rule.weekdays.contains(currentDayOfWeek)
    }
    if (!rule.isRecurring && rule.isActive && rule.isEndTimeSet) {
        result = currentTime.before(rule.endTime)
    }
    return result
}

