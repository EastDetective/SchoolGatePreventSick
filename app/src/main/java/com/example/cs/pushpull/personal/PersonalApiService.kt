package com.example.cs.pushpull.personal

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.personal.model.ProfileModel
import com.example.cs.pushpull.personal.model.TimeTableModel
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PersonalApiService {

    @GET("student/{studentUUID}")
    fun getFullData(@Path("studentUUID") studentUUID: String): Observable<ProfileModel.Full>

    @Multipart
    @POST("studentImages/{imageType}")
    fun uploadImage(@Path("imageType") imageType: String, @Part("studentID") requestBody: RequestBody, @Part file: MultipartBody.Part): Completable

    @GET("course/getAllCourseTable/{studentUUID}")
    fun getDate(@Path("studentUUID") studentUUID: String): Observable<List<String>>

    @GET("course/getCourseTable/{studentUUID}/{dateYMD}")
    fun getDateCourse(@Path("studentUUID") studentUUID: String, @Path("dateYMD") dateYMD: String): Observable<List<TimeTableModel.CourseDate>>

    @Headers("Content-Type:application/json")
    @PUT("student/update/{studentId}")
    fun updateUserData(@Path("studentId") studentId: String, @Body updateData: ProfileModel.Full): Completable

    companion object {
        fun create(): PersonalApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(PersonalApiService::class.java)
        }
    }
}