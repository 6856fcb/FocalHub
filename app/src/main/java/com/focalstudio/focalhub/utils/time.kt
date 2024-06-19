package com.focalstudio.focalhub.utils

import java.text.DateFormat
import java.util.Calendar
import java.util.Date

fun isCurrentTimeBeforeThisTime(time: Date) : Boolean {
    val currentTime = Calendar.getInstance().time
    return currentTime.before(time)
}
fun isCurrentTimeAfterThisTime(time: Date) : Boolean {
    val currentTime = Calendar.getInstance().time
    return currentTime.after(time)
}
fun isTodayInThisWeekdayList(weekdays : List<Int>) : Boolean {
    val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return weekdays.contains(currentDayOfWeek)
}

fun isCurrentTimeAndDayInThisWindow(timeStart: Date, timeEnd: Date, weekdays: List<Int>): Boolean {
    return isCurrentTimeAfterThisTime(timeStart)
            && isCurrentTimeBeforeThisTime(timeEnd)
            && isTodayInThisWeekdayList(weekdays)
}
fun getStartOfDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
fun getTimeInMillis(
    year: Int,
    month: Int,
    day: Int,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0
): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, hour, minute, second) // Month is 0-based in Calendar
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
