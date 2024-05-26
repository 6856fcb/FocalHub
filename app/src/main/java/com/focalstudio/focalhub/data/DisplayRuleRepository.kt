package com.focalstudio.focalhub.data

import android.content.Context
import com.focalstudio.focalhub.data.model.DisplayRule
import java.util.*

interface DisplayRuleRepository {
    suspend fun getRules(): List<DisplayRule>
    fun setRuleChangeListener(listener: () -> Unit)
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
    private val rules = mutableListOf<DisplayRule>()
    private var ruleChangeListener: (() -> Unit)? = null

    init {
        loadDummyRules()
    }

    private fun loadDummyRules() {
        rules.add(
            DisplayRule(
                id = 1,
                name = "Instagram Tester",
                appList = listOf("com.instagram.android"),
                isBlacklist = true,
                isActive = true,
                isRecurring = true,
                startTime = Date(),
                endTime = Date(),
                weekdays = listOf(0, 1, 2, 3, 4, 5, 6)
            )
        )
    }

    override suspend fun getRules(): List<DisplayRule> {
        return rules.toList()
    }

    override fun setRuleChangeListener(listener: () -> Unit) {
        ruleChangeListener = listener
    }
}
