package com.focalstudio.focalhub.data


import android.content.Context
import com.focalstudio.focalhub.data.model.DisplayRule
import com.focalstudio.focalhub.data.model.UsageRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface RuleRepository {
    suspend fun getRules(): List<DisplayRule>
    fun addRule(rule: DisplayRule)
    fun updateRule(rule: DisplayRule)
    fun deleteRule(ruleId: Int)
    fun setRuleChangeListener(listener: () -> Unit)
    fun updateRuleIsActive(ruleId: Int, isActive: Boolean)
    suspend fun getUsageRules(): List<UsageRule>
    fun addUsageRule(usageRule: UsageRule)
    fun updateUsageRule(usageRule: UsageRule)
    fun deleteUsageRule(usageRuleId: Int)
    suspend fun getUsageRuleById(usageRuleId: Int): UsageRule?
    suspend fun getDisplayRuleById(ruleId: Int): DisplayRule?
}

object RuleRepositoryProvider {
    private var instance: RuleRepository? = null

    fun getInstance(context: Context): RuleRepository {
        return instance ?: synchronized(this) {
            //instance ?: InMemoryDisplayRuleRepository().also { instance = it }
            instance ?: RoomRuleRepository(context).also { instance = it }
        }
    }
}

class RoomRuleRepository(private val context: Context) : RuleRepository {
    private val database = RuleDatabase.getInstance(context)
    private val displayRuleDao = database.displayRuleDao()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val usageRuleDao = database.usageRuleDao()

    //Usage Rules

    override suspend fun getUsageRules(): List<UsageRule> {
        return usageRuleDao.getAllUsageRules()
    }

    override suspend fun getUsageRuleById(usageRuleId: Int): UsageRule {
        return usageRuleDao.getUsageRuleById(usageRuleId)
    }

    override suspend fun getDisplayRuleById(ruleId: Int): DisplayRule? {
        TODO("Not yet implemented")
    }

    override fun addUsageRule(usageRule: UsageRule) {
        ioScope.launch {
            this@RoomRuleRepository.usageRuleDao.insert(usageRule)
        }
    }

    override fun updateUsageRule(usageRule: UsageRule) {
        ioScope.launch {
            this@RoomRuleRepository.usageRuleDao.update(usageRule)
        }
    }

    override fun deleteUsageRule(usageRuleId: Int) {
        ioScope.launch {
            this@RoomRuleRepository.usageRuleDao.delete(
                this@RoomRuleRepository.usageRuleDao.getUsageRuleById(
                    usageRuleId
                )
            )
        }
    }

    override suspend fun getRules(): List<DisplayRule> {
        return this.displayRuleDao.getAllRules()
    }

    override fun addRule(rule: DisplayRule) {
        ioScope.launch {
            this@RoomRuleRepository.displayRuleDao.insert(rule)
        }
    }

    override fun updateRule(rule: DisplayRule) {
        ioScope.launch {
            this@RoomRuleRepository.displayRuleDao.update(rule)
        }
    }

    override fun deleteRule(ruleId: Int) {
        ioScope.launch {
            this@RoomRuleRepository.displayRuleDao.delete(
                this@RoomRuleRepository.displayRuleDao.getDisplayRuleById(
                    ruleId
                )
            )
        }
    }


    override fun setRuleChangeListener(listener: () -> Unit) {
        //
    }

    override fun updateRuleIsActive(ruleId: Int, isActive: Boolean) {
        ioScope.launch {
            val rule = this@RoomRuleRepository.displayRuleDao.getDisplayRuleById(ruleId)
            rule.isActive = isActive
            this@RoomRuleRepository.displayRuleDao.update(rule)
        }
    }
}