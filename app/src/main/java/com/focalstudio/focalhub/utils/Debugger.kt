package com.focalstudio.focalhub.utils

import android.util.Log
fun log(content: Any, optional : String = "Debugger") {
    Log.d(optional, content.toString())
}