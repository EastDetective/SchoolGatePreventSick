package com.example.cs.pushpull.notification.model

object NotificationModel {

    data class Notice(
        val courseType: Int,
        val content: String,
        val title: String,
        val pushTime: String,
        var isExtended: Boolean = false
    )
}