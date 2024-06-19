package com.focalstudio.focalhub.utils

import android.app.usage.UsageStatsManager
import android.content.Context

fun getAppUsageTimeInSeconds(context: Context, packageName: String, startTime: Long? = null, endTime: Long? = null): Long {
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

    return totalTime / 1000 // Convert milliseconds to seconds
}