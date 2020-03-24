package com.example.cs.pushpull

import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class LoginCheck(
    val studentID: String,
    val password: String
)

data class LoginSuccess(
    val studentName: String,
    val studentUUID: String
)

interface LoginApiService {

    @Headers("Content-Type:application/json")
    @POST("loginCheck")
    fun login(@Body check: LoginCheck): Observable<LoginSuccess>

    companion object {
        fun create(): LoginApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(LoginApiService::class.java)
        }
    }

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @PUT("student/updatePushToken/{studentID}/token/{deviceToken}")
    fun putToken(@Path("studentID") studentID: String, @Path("deviceToken") deviceToken: String): Completable
}