package com.example.cs.pushpull.school.model

import java.util.*

object RecommendModel {

    data class Recommend(

        val campus: Int,
        val courseType: Int,
        val courseIntroduction: String,
        val recommendReason: String,
        val verificationRejectedReason: String,
        val proposerID: String,
        val voteEndTime: Date,
        val verifyState: Int,
        val numberOfVoters: Int,
        val verifiedTime: Date,
        val voteStartTime: Date,
        val recommendTime: Date,
        val courseName: String,
        val id: String
    )

    data class RecommendPost(
        val courseName: String,
        val courseType: Int,//語言證照其他
        val campus: Int,//學校
        val recommendReason: String,
        val courseIntroduction: String,
        val proposerID: String,
        val voteTime: String,
        val verifiedTime: String,
        val verifyState: Int,
        val verificationRejectedReason: String,
        val numberOfVoters: Int,
        val voteStartTime: String,
        val voteEndTime: String,
        val recommendTime: String
    )


}