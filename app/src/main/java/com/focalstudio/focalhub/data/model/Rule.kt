package com.focalstudio.focalhub.data.model

import java.util.Date

data class DisplayRule(
        var id: Int,
        var name: String,
        var appList: List<String>,
        var isBlacklist: Boolean,
        var isActive: Boolean,
        var isDisabled: Boolean,
        var isRecurring: Boolean,
        var isEndTimeSet: Boolean,
        var startTime: Date,
        var endTime: Date,
        var weekdays: List<Int> // 0 for Sunday, 1 for Monday, ..., 6 for Saturday
    )
