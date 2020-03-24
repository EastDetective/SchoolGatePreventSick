package com.example.cs.pushpull.school.model

object TakeLeaveModel {
    data class TakeLeave(
        val enrollPost: Int,
        val courseCost: Int,
        val courseEndTime: String,
        val courseTeacher: String,
        val campus: Int,
        val other: String,
        val applyMethod: Int,
        val courseDate: List<String>,
        val courseType: Int,
        val coursePlace: String,
        val numberOfApply: Int,
        val courseStartTime: String,
        val numberOfStudents: Int,
        val courseState: Int,
        val courseTime: String,
        val id: String,
        val applyStartTime: String,
        val courseName: String,
        val applyEndTime: String,
        val courseIntroduction: String,
        val targetStudent: Int
    )

    data class TakeLeavePost(
        val leaveDate: String,
        val leaveReason: Int,
        val detailReason: String,
        val courseID: String,
        val studentID: String
    )

    data class TakeLeaveDate(
        val courseDate: List<String>
    )
}