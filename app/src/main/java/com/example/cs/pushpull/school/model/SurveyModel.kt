package com.example.cs.pushpull.school.model

object SurveyModel {

    // Get Question
    data class SurveyQuestion(
        val kind: List<Int>,
        val quest: List<String>,
        val title: List<String>,
        val scale: List<List<String>>
    )

    // Post Answer
    data class SurveyAnswer(
        val courseID: String,
        val quest: List<String>,
        val answer: List<String>,
        val kind: List<Int>,
        val scale: List<List<String>>,
        val studentID: String
    )
}