package com.focalstudio.focalhub.data

import android.content.Context
import java.util.*

data class DisplayRule(
    val appList: List<String>,
    val isBlacklist: Boolean,
    val isActive: Boolean,
    val isRecurring: Boolean,
    val startTime: Date,
    val endTime: Date,
    val weekdays: List<Int> // 0 for Sunday, 1 for Monday, ..., 6 for Saturday
)

interface DisplayRuleRepository {
    suspend fun getRules(): List<DisplayRule>
}

object DisplayRuleRepositoryProvider {
    private var instance: DisplayRuleRepository? = null

    fun getInstance(context: Context): DisplayRuleRepository {
        return instance ?: synchronized(this) {
            instance ?: DummyDisplayRuleRepository().also { instance = it }
        }
    }
}

// Dummy implementation of DisplayRuleRepository
class DummyDisplayRuleRepository : DisplayRuleRepository {
    override suspend fun getRules(): List<DisplayRule> {
        // Here you can fetch the rules from a database, API, or any other source
        // For now, let's return some dummy data
        return listOf(
            DisplayRule(
                appList = listOf("com.instagram.android"),
                isBlacklist = false,
                isActive = true,
                isRecurring = true,
                startTime = Date(),
                endTime = Date(),
                weekdays = listOf(0, 1, 2, 3, 4, 5, 6)
            )
        )
    }
}
