package com.focalstudio.focalhub.utils

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
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

    // Add more checks...

    return (usageRule.maxUsageDurationInSeconds <= usageTimeSumInSeconds) && usageRule.restrictUsageTimePerApp
}

@RequiresApi(Build.VERSION_CODES.Q)
fun isUsageStatsPermissionGranted(context: Context): Boolean {
    val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOpsManager.unsafeCheckOpNoThrow( // Better ways to check for high level permissions?
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}
