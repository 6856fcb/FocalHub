package com.focalstudio.focalhub.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.focalstudio.focalhub.data.model.DisplayRule
import com.focalstudio.focalhub.data.model.DisplayRuleDao
import com.focalstudio.focalhub.data.model.UsageRule
import com.focalstudio.focalhub.data.model.UsageRuleDao

@Database(entities = [DisplayRule::class, UsageRule::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RuleDatabase : RoomDatabase() {
    abstract fun displayRuleDao(): DisplayRuleDao
    abstract fun usageRuleDao(): UsageRuleDao

    companion object {
        @Volatile
        private var INSTANCE: RuleDatabase? = null

        fun getInstance(context: Context): RuleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RuleDatabase::class.java,
                    "rule_database"
                )
                    .fallbackToDestructiveMigration() // Destroy and recreate the database if schema changed
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
