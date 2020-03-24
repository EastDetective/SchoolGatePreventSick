package com.example.cs.pushpull.personal.model

object FormModel {

    data class Apply(
        val name: String,
        val state: Int,
        val pushDate: String
    )

    data class LicenseGrade(
        val verifiedDate: String,
        val state: Int,
        val name: String,
        var verificationRejectedReason: String
    )

    data class ApplyTime(
        val startTime:String,
        val endTime : String,
        val status: Int
    )
}