package com.reselling.visionary.utils

import android.app.Activity
import android.content.Intent

fun Activity.move(destination: Class<*>, addFlags: Boolean = true) {
    val destinationIntent = Intent(this, destination)

    if (addFlags) {
        destinationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    this.startActivity(destinationIntent)
    this.finish()
}