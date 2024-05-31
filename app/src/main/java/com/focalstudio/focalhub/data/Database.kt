package com.focalstudio.focalhub.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.focalstudio.focalhub.data.Converters

@Database(entities = [DisplayRule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DisplayRuleDatabase : RoomDatabase() {
    abstract fun displayRuleDao(): DisplayRuleDao

    companion object {
        @Volatile
        private var INSTANCE: DisplayRuleDatabase? = null

        fun getInstance(context: Context): DisplayRuleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DisplayRuleDatabase::class.java,
                    "display_rule_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
