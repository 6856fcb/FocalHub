package com.focalstudio.focalhub.utils

import java.util.Calendar
import java.util.Date

fun isCurrentTimeBeforeThisTime(time: Date) : Boolean {
    val currentTime = Calendar.getInstance().time
    return currentTime.before(time)
}
fun isCurrentTimeAfterThisTime(time: Date) : Boolean {
    val currentTime = Calendar.getInstance().time
    return currentTime.before(time)
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