package com.focalstudio.focalhub.filtering

import android.graphics.drawable.Drawable


data class App(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

val filterFreeTime = listOf(
    "com.google.android.youtube", // YouTube
    "com.google.android.googlequicksearchbox", // Google
    "com.instagram.android",
    "com.zhiliaoapp.musically"
)

val filterStudyTime = listOf(
    "com.google.android.gm", // YouTube
    "com.google.android.googlequicksearchbox", // Google
)

