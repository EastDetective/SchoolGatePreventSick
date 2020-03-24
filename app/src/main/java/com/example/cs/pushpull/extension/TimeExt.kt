package com.example.cs.pushpull.extension

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Date.toISO8601UTC() : String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").apply {
        timeZone = TimeZone.getDefault()
    }.format(this)
}

fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    return SimpleDateFormat(dateFormat, Locale.getDefault()).apply {
        this.timeZone = timeZone
    }.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    return SimpleDateFormat(dateFormat, Locale.getDefault()).apply {
        this.timeZone = timeZone
    }.format(this)
}

fun now(): Date {
    return Calendar.getInstance().time
}