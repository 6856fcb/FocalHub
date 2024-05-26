package com.focalstudio.focalhub.data

import android.content.Context
import com.focalstudio.focalhub.data.model.DisplayRule
import java.util.*



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

class DummyDisplayRuleRepository : DisplayRuleRepository {
    override suspend fun getRules(): List<DisplayRule> {
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
