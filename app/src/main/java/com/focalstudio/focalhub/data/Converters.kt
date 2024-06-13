package com.focalstudio.focalhub.data

import android.util.Log
import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return value.split(",").map { it }
    }

    @TypeConverter
    fun stringListToString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromIntList(value: String): List<Int> {
        Log.d(value, "checkpoint")
        return value.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun intListToString(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}
