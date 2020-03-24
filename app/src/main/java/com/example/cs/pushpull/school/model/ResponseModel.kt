package com.example.cs.pushpull.school.model

import com.example.cs.pushpull.extension.now
import com.example.cs.pushpull.extension.toISO8601UTC

object ResponseModel {

    data class Response(
        val teacherResponse: String,
        val teacherResponseTime: String,
        val courseID: String,
        val studentID: String,
        val evaluationID: String,
        val evaluationTime: String,
        val studentContent: String,
        val state: Int
    )

    data class ResponsePost(
        val state: Int,
        val studentContent: String,
        val evaluationTime: String,
        val courseID: String,
        val studentID: String,
        val teacherResponse: String = "",
        val teacherResponseTime: String = now().toISO8601UTC()
    )
}