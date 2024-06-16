package com.focalstudio.focalhub.utils

import android.util.Log
import com.focalstudio.focalhub.data.model.App
import com.focalstudio.focalhub.data.model.DisplayRule
import com.focalstudio.focalhub.data.model.UsageRule


fun getFilteredApps (

    allApps: List<App>,
    displayRules: List<DisplayRule>

): List<App>{

    val blacklistedApps = mutableSetOf<String>()
    val whitelistedApps = mutableSetOf<String>()

    // Add apps to corresponding list
    for (rule in displayRules) {
        if (rule.isActive) {
            if (rule.isBlacklist) {
                blacklistedApps.addAll(rule.appList)
            } else {
                whitelistedApps.addAll(rule.appList)
            }
        }
    }

    val filteredApps = mutableListOf<App>()

    // Apply display priorities in order
    if (blacklistedApps.isNotEmpty()) {
        for (app in allApps) {
            if (blacklistedApps.contains(app.packageName)) {
                // 1.) Always Block blacklisted apps -> Don't show
                continue

            } else if (whitelistedApps.contains(app.packageName)) {
                // 2.) If not blacklisted check if app is whitelisted -> Show App
                filteredApps.add(app)

            } else if (whitelistedApps.isEmpty())
            // 3.) No whitelisted items found and app not blocked -> Show App
            {filteredApps.add(app)}
        }
    } else {
        // No blacklisted apps, allow only whitelisted apps
        for (app in allApps) {
            if (whitelistedApps.contains(app.packageName)) {
                // Allow whitelisted apps
                filteredApps.add(app)
            }
        }
    }
    return filteredApps
}
fun applyDisplayRules(apps: List<App>, displayRules: List<DisplayRule>): List<App> {

    if (displayRules.isEmpty()) {return apps} // Default Rule applies

    val filteredApps = getFilteredApps(apps, displayRules)
    return filteredApps.ifEmpty { apps }
}

fun shouldDisplayRuleBeCurrentlyActive (rule: DisplayRule): Boolean {

    return if (rule.isRecurring && !rule.isDisabled) {
        isCurrentTimeAndDayInThisWindow(
            timeStart = rule.startTime,
            timeEnd = rule.endTime,
            weekdays = rule.weekdays)

    } else if (rule.isEndTimeSet && rule.isActive && !rule.isRecurring) {
        isCurrentTimeBeforeThisTime(rule.endTime)

    } else {
        rule.isActive
    }
}

fun shouldNonLinkedUsageRuleBeCurrentlyActive (rule: UsageRule): Boolean {

    // Maybe one function for both rule types?

    return if (rule.isRecurring && !rule.isManuallyDisabled) {

        isCurrentTimeAndDayInThisWindow(rule.timeWindowStartTime, rule.timeWindowEndTime, rule.weekdays)

    } else if (rule.isRestrictedUntilEndTime && rule.isCurrentlyActive && !rule.isRecurring) {

        isCurrentTimeBeforeThisTime(rule.timeWindowEndTime)

    } else {
        rule.isCurrentlyActive
    }
}



