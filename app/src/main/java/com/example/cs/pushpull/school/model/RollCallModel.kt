package com.example.cs.pushpull.school.model

object RollCallModel {

    // Used For Roll-Call : GET courses
    data class CourseSimple(
        val id: String,
        val courseName: String
    )

    // Used For Roll-Call : PUT verifying string
    data class Verification(
        val courseID: String,
        val inCourseTime: String,
        val studentID: String,
        val verifyString: String
    )

    data class RollCallLoginResponse(
        val account: String,
        val rcaccountID: String,
        val password: String,
        val courseCode: String
    )

    // For Teacher
    data class AddRollCall(
        val rollCallDate: String,
        val courseID: String,
        val studentID: String,
        val scanString: String,
        val rcaccountID: String,
        val courseCode: String
    )

    data class Account(
        var account: String,
        var password: String,
        var courseCode: String
    )
}