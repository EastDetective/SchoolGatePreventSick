package com.example.cs.pushpull.school.recommend

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.school.model.RecommendModel
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RecommendApiService {

    @Headers("Content-Type:application/json")
    @POST("recommendcourse")
    fun postRecommend(@Body recommend: RecommendModel.RecommendPost): Completable

    @GET("recommendcourse")
    fun getRecommend(): Observable<List<RecommendModel.Recommend>>

    companion object {
        fun create(): RecommendApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(RecommendApiService::class.java)
        }
    }
}