package com.example.cs.pushpull.game

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.game.model.LuckyDayModel
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface GameApiService {

    @GET("lottery/availableLottery/{studentUUID}")
    fun getNotUsedTicketNumber(@Path("studentUUID") studentUUID: String): Observable<List<LuckyDayModel.TicketNotUsed>>

    @GET("point/getAllPoints/{studentUUID}")
    fun getTotalPoint(@Path("studentUUID") studentUUID: String): Observable<LuckyDayModel.AllPoint>

    @GET("lottery/isUsedLottery/{studentUUID}")
    fun getHistory(@Path("studentUUID") studentUUID: String): Observable<List<LuckyDayModel.TicketIsUsed>>

    @Headers("Content-Type:application/json")
    @PUT("lottery/changeToUsed/{lotteryUUID}")
    fun putLotteryChanged(@Path("lotteryUUID") lotteryUUID: String): Completable

    @GET("point/getLuckyDay/{studentUUID}")
    fun getLuckyDayResult(@Path("studentUUID") studentUUID : String): Observable<LuckyDayModel.LuckyResult>

    @GET("point")
    fun postIncreaseePoint(): Completable

    @POST("lottery/changePoint/{studentUUID}")
    fun postPoint(@Path("studentUUID") studentUUID: String, @Body total: LuckyDayModel.PointPost): Completable

    companion object {
        fun create(): GameApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(GameApiService::class.java)
        }
    }
}