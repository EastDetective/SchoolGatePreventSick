package com.example.cs.pushpull.school.leave

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.school.model.TakeLeaveModel
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TakeLeaveApiService {

    @Headers("Content-Type:application/json")
    @POST("leave")
    fun postTakeLeave(@Body takeLeave: TakeLeaveModel.TakeLeavePost): Completable

    @GET("student/{studentId}/inCourses")
    fun getTakeLeave(@Path("studentId") studentId: String ): Observable<List<TakeLeaveModel.TakeLeave>>

    @GET("course/getCourseDate/{courseId}")
    fun getTakeLeaveDate(@Path("courseId") courseId: String ): Observable<TakeLeaveModel.TakeLeaveDate>

    companion object {
        fun create(): TakeLeaveApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(TakeLeaveApiService::class.java)
        }
    }
}