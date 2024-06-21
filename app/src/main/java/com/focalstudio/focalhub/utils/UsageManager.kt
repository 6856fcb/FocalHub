package com.focalstudio.focalhub.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import com.focalstudio.focalhub.data.model.UsageRule

fun getAppUsageTimeInSeconds(context: Context, packageName: String, startTime: Long? = null, endTime: Long? = null): Int {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val end = endTime ?: System.currentTimeMillis()
    val start = startTime ?: getStartOfDay()

    val usageStatsList = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY, start, end
    )

    var totalTime = 0L

    for (usageStats in usageStatsList) {
        if (usageStats.packageName == packageName) {
            totalTime += usageStats.totalTimeInForeground
        }
    }
    return (totalTime / 1000).toInt() // Convert milliseconds to seconds
}

fun isAppUsagePermittedByUsageRule(usageRule: UsageRule, context: Context) : Boolean {
    var usageTimeSumInSeconds = 0
    for (packageName in usageRule.appList) {
        usageTimeSumInSeconds += getAppUsageTimeInSeconds(context, packageName)
    }

    if(!isUsageStatsPermissionGranted(context)) {
        log("No usage stats permission granted", " Permission ERROR")
        return true
    }
    // Add more checks...
    return (usageRule.maxUsageDurationInSeconds >= usageTimeSumInSeconds) || !usageRule.restrictUsageTimePerApp
}

fun isUsageStatsPermissionGranted(context: Context): Boolean {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val currentTime = System.currentTimeMillis()
    val stats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        currentTime - 1000 * 60 * 60,
        currentTime
    )
    return stats.isNotEmpty()
}
