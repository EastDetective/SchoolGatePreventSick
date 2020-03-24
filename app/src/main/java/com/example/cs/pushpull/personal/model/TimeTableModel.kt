package com.example.cs.pushpull.personal.model

object TimeTableModel {

    data class CourseDate(
        val courseType :Int,
        val courseId:String,
        val courseDate:String,
        val courseName:String
    )
}