package com.focalstudio.focalhub.utils

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
                delay(1000)
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

        if(currentAppPackage != null) {
            if (!isAppAllowed(currentAppPackage, this)) {

                log("App $currentAppPackage is not allowed", "AppUsageMonitoringService")
                launchLauncher()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val handler = Handler(Looper.getMainLooper())

    private fun launchLauncher() {
        handler.post {
            val launcherIntent = packageManager.getLaunchIntentForPackage("com.focalstudio.focalhub")
            launcherIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launcherIntent)
            //Toast.makeText(applicationContext, "Daily Usage Limit Reached", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun isAppAllowed(packageName: String, context: Context): Boolean {
        val usageRules = RuleRepositoryProvider.getInstance(context).getUsageRules()
        if (usageRules.isNotEmpty()) {
            for (usageRule in usageRules) {
                if (usageRule.appList.contains(packageName)) {
                    val ruleActive = shouldNonLinkedUsageRuleBeCurrentlyActive(usageRule)
                    val noTimeLeft = !isAppUsagePermittedByUsageRule(usageRule, context)
                    // Usage Rule active (In set time windows) and not linked
                    return !((ruleActive &&
                            (usageRule.isRestrictedUntilEndTime
                                    || usageRule.isRecurring
                                    || (noTimeLeft && usageRule.restrictUsageTimePerApp))) &&

                            ((noTimeLeft && usageRule.restrictUsageTimePerApp) ||
                                    !usageRule.restrictUsageTimePerApp))
                }
            }

        } else {
            return true
        }
        return true
    }

}
