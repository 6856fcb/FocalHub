package com.focalstudio.focalhub.data.model

import androidx.room.*
import com.focalstudio.focalhub.data.Converters
import java.util.Date

@Entity(tableName = "usage_rules")
@TypeConverters(Converters::class)
data class UsageRule(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var name: String = "",
        var appList: List<String> = listOf(),
        var ruleMode: Int = 0,
        var isLinkedToDisplayRule: Boolean = false,
        @ColumnInfo(name = "linked_rule_id")
        var linkedRuleId: Int = 0,
        //var onlyImportApps: Boolean = true,
        var isCurrentlyActive: Boolean = false,
        var isManuallyDisabled: Boolean = false,
        var restrictUsageTimePerApp: Boolean = false,
        var maxUsageDurationInSeconds: Int = 0,
        var useMaxSessionTimes: Boolean = false,
        var displaySessionDurationDialog: Boolean = false,
        var maxSessionDurationInSeconds: Int = 0,
        var isRestrictedUntilEndTime: Boolean = false,
        var timeWindowStartTime: Date = Date(0),
        var timeWindowEndTime: Date = Date(0),
        var weekdays: List<Int> = listOf(),
        var isRecurring: Boolean = false,
)

@Entity(tableName = "display_rules")
@TypeConverters(Converters::class)
data class DisplayRule(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var name: String = "",
        var appList: List<String> = listOf(),
        var isBlacklist: Boolean = false,
        var isActive: Boolean = false,
        var isDisabled: Boolean = false,
        var isRecurring: Boolean = false,
        var isEndTimeSet: Boolean = false,
        var startTime: Date = Date(0),
        var endTime: Date = Date(0),
        var weekdays: List<Int> = listOf(),
        @Ignore
        var usageRule: List<UsageRule?> = listOf()
)

@Dao
interface UsageRuleDao {
        @Insert
        suspend fun insert(usageRule: UsageRule)

        @Update
        suspend fun update(usageRule: UsageRule)

        @Delete
        suspend fun delete(usageRule: UsageRule)

        @Query("SELECT * FROM usage_rules ORDER BY id ASC")
        suspend fun getAllUsageRules(): List<UsageRule>

        @Query("SELECT * FROM usage_rules WHERE id = :ruleId")
        suspend fun getUsageRuleById(ruleId: Int): UsageRule
}

// Update DisplayRuleDao to include foreign key reference
@Dao
interface DisplayRuleDao {
        @Insert
        suspend fun insert(displayRule: DisplayRule)

        @Update
        suspend fun update(displayRule: DisplayRule)

        @Delete
        suspend fun delete(displayRule: DisplayRule)

        @Query("SELECT * FROM display_rules ORDER BY id ASC")
        suspend fun getAllRules(): List<DisplayRule>

        @Query("SELECT * FROM display_rules WHERE id = :ruleId")
        suspend fun getDisplayRuleById(ruleId: Int): DisplayRule
}

// Update DisplayRuleDatabase to include both entities
