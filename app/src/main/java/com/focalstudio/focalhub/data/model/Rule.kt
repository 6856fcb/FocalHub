package com.focalstudio.focalhub.data.model

import java.util.Date

data class DisplayRule(
        val appList: List<String>,
        val isBlacklist: Boolean,
        var isActive: Boolean,
        val isRecurring: Boolean,
        val startTime: Date,
        val endTime: Date,
        val weekdays: List<Int> // 0 for Sunday, 1 for Monday, ..., 6 for Saturday
    )
