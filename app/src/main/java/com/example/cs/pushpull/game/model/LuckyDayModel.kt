package com.example.cs.pushpull.game.model

import java.util.*
import kotlin.collections.ArrayList

object LuckyDayModel {
    data class AllPoint(
        val point: ArrayList<PointList>,
        val totalPoint: Int
    )
    data class PointList(
        val pointReason: String,
        val getPoint: Int
    )

    data class TicketIsUsed(
        var isUsedDate: String
    )

    data class TicketNotUsed(
        var id: String
    )

    data class PointPost(
        var totalPoints: Int,
        var totalLottery: Int
    )

    data class LuckyResult(
        var point : Int,
        var index : Int,
        var totalPoint : Int
    )

}