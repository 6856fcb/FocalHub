package com.focalstudio.focalhub.data.model

import androidx.room.*
import com.focalstudio.focalhub.data.Converters
import java.util.Date

@Entity(tableName = "display_rules")
@TypeConverters(Converters::class)
data class DisplayRule(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo(name = "name")
        var name: String = "",
        @ColumnInfo(name = "app_list")
        var appList: List<String> = listOf(),
        @ColumnInfo(name = "is_blacklist")
        var isBlacklist: Boolean = false,
        @ColumnInfo(name = "is_active")
        var isActive: Boolean = false,
        @ColumnInfo(name = "is_disabled")
        var isDisabled: Boolean = false,
        @ColumnInfo(name = "is_recurring")
        var isRecurring: Boolean = false,
        @ColumnInfo(name = "is_end_time_set")
        var isEndTimeSet: Boolean = false,
        @ColumnInfo(name = "start_time")
        var startTime: Date = Date(0),
        @ColumnInfo(name = "end_time")
        var endTime: Date = Date(0),
        @ColumnInfo(name = "weekdays")
        var weekdays: List<Int> = listOf()
)


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
        suspend fun getRuleById(ruleId: Int): DisplayRule
}
