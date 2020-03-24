package com.example.cs.pushpull.notification

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.notification.model.NotificationModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface NotificationApiService {

    @GET("student/getNormalNotice")
    fun getNormalNotice(): Observable<List<NotificationModel.Notice>>

    @GET("student/getStudentNotice/{studentUUID}")
    fun getStudentNotice(@Path("studentUUID") studentUUID: String): Observable<List<NotificationModel.Notice>>

    @GET("student/getCourseNotice/{studentUUID}")
    fun getCourseNotice(@Path("studentUUID") studentUUID: String): Observable<List<NotificationModel.Notice>>

    companion object {
        fun create(): NotificationApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(NotificationApiService::class.java)
        }
    }
}