package com.example.cs.pushpull.school.model

object CourseModel {

    // General Case for Course
    data class Course(

        // Fixed Value
        val coursePlace: String, // 上課地點
        val firstNumberOfStudents: Int,
        val firstApplyStartTime: String,
        val firstApplyEndTime: String,
        val secondNumberOfStudents: Int,
        val secondApplyStartTime: String,
        val secondApplyEndTime: String,
        val enrollPost: Int,
        val applyMethod: Int,
        val voteStartTime: String,
        val voteEndTime: String,
        val id: String,
        val status: Int,
        val campus: Int, // 0: 博愛 1: 天母
        val other: String, // 備註
        val targetStudent: Int, // 招生對象
        val courseDate: List<String>,
        val satisfactionBool: Int,
        val courseTeacher: String,
        val courseType: Int,
        val courseCost: Int,
        val courseName: String,
        val courseIntroduction: String,
        val studentState: Int,
        val applyResult: Int, // 0 = 正取 1 = 備取

        // Changeable Value (Can be changed by App)
        var numberOfVoters: Int,
        var numberOfApply: Int, // 實收人數

        // State to Student (Editable)
        var studentVote: Int, // 有無投票
        var studentInCourse: Int, // 是否上課中
        var studentFavorite: Int, // 有無收藏
        var studentApply: Int, // 有無報名

        var isExtended: Boolean = false
    )

    data class CoursePost(
        var courseWeek: Array<Int>
    )

    data class Banner(
        var id: String,
        var title: String,
        var content: String,
        var picture: String,
        var sort: Int,
        var webPage: String
    )

    data class Apply(
        var state: Int,
        var applyNumbers: Int
    )
}