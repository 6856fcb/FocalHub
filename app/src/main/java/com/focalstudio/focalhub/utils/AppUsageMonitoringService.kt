package com.focalstudio.focalhub.utils

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.focalstudio.focalhub.data.RuleRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppUsageMonitoringService : Service() {

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var lastAppPackage: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            while (true) {

                monitorAppUsage()
                delay(5000) // Check every 5 seconds (Maybe set in settings later on)
            }
        }
        return START_STICKY
    }

    private suspend fun monitorAppUsage() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()

        // Calculate time for last hour
        val startTime = currentTime - 1000 * 60 * 60

        // Get the usage events last hour
        val usageEvents = usageStatsManager.queryEvents(startTime, currentTime)

        var currentAppPackage: String? = null

        // Iterate through events to find latest foreground app
        while (usageEvents.hasNextEvent()) {

            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentAppPackage = event.packageName
            }
        }
        log("Current app package: $currentAppPackage")


        if (
            !isAppAllowed(currentAppPackage!!, this)) {
                launchLauncher()
            }





        /*currentAppPackage?.let { appPackageName -> log("appPackageName: $appPackageName lastAppPackage: $lastAppPackage")
            if (appPackageName != lastAppPackage) {
                log("Test11111")
                lastAppPackage = appPackageName
                if (!isAppAllowed(appPackageName, this)) {
                    log("App $appPackageName is not allowed", "AppUsageMonitoringService")
                    launchLauncher()
                }
            }
        }*/
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun launchLauncher() {
        val launcherIntent = packageManager.getLaunchIntentForPackage("com.focalstudio.focalhub")
        launcherIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launcherIntent)
    }

    private suspend fun isAppAllowed(packageName: String, context: Context): Boolean {
        val usageRules = RuleRepositoryProvider.getInstance(context).getUsageRules()
        if (usageRules.isNotEmpty()) {
            var appFoundInARule = false

            for (usageRule in usageRules) {


                if (usageRule.appList.contains(packageName)) {
                    appFoundInARule = true
                    val ruleActive = shouldNonLinkedUsageRuleBeCurrentlyActive(usageRule)
                    val noTimeLeft = !isAppUsagePermittedByUsageRule(usageRule, context)
                    // Usage Rule active (In set time windows) and not linked
                    if ((ruleActive &&
                                (usageRule.isRestrictedUntilEndTime
                                        || usageRule.isRecurring
                                        || (noTimeLeft && usageRule.restrictUsageTimePerApp))) &&

                        ((noTimeLeft && usageRule.restrictUsageTimePerApp) ||
                                !usageRule.restrictUsageTimePerApp)
                    ) {
                        if (noTimeLeft) {
                            return false
                        } else {
                            return false
                        }
                    } else {
                        return true
                    }
                }
            }
            if (!appFoundInARule) {
                return true

            }
        } else {
            return true
        }
        return true
    }

}
