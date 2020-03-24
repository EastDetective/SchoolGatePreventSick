package com.example.cs.pushpull.personal.form

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.personal.model.FormModel
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface FormApplyApiService {

    @Multipart
    @POST("{imageType}")
    fun uploadImageForGrade(
        @Path("imageType") imageType: String,
        @Part("applyYear") requestBody1: RequestBody,
        @Part("applySemester") requestBody2: RequestBody,
        @Part("gradeDescription") requestBody3: RequestBody,
        @Part("studentID") requestBody4: RequestBody,
        @Part("studentDepartment") requestBody5: RequestBody,
        @Part("studentRegisterDate") requestBody6: RequestBody,
        @Part("studentAddress") requestBody7: RequestBody,
        @Part("studentPhoneNumber") requestBody8: RequestBody,
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part,
        @Part file3: MultipartBody.Part,
        @Part file4: MultipartBody.Part,
        @Part file5: MultipartBody.Part
    ): Completable

    @Multipart
    @POST("{imageType}")
    fun uploadImageForLicense(
        @Path("imageType") imageType: String,
        @Part("licenseName") requestBody1: RequestBody,
        @Part("licenseType") requestBody2: RequestBody,
        @Part("licenseDate") requestBody3: RequestBody,
        @Part("studentID") requestBody4: RequestBody,
        @Part("studentDepartment") requestBody5: RequestBody,
        @Part("studentRegisterDate") requestBody6: RequestBody,
        @Part("studentAddress") requestBody7: RequestBody,
        @Part("studentPhoneNumber") requestBody8: RequestBody,
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part,
        @Part file3: MultipartBody.Part,
        @Part file4: MultipartBody.Part,
        @Part file5: MultipartBody.Part
    ): Completable

    @GET("license/pushLicenseGrade/{studentUUID}")
    fun getMyApply(@Path("studentUUID") studentUUID: String): Observable<List<FormModel.Apply>>

    @GET("setTime/getGradeTime")
    fun getGradeTime(): Observable<FormModel.ApplyTime>

    @GET("license/passLicenseGrade/{studentUUID}")
    fun getPass(@Path("studentUUID") studentUUID: String): Observable<List<FormModel.LicenseGrade>>

    @GET("license/declineLicenseGrade/{studentUUID}")
    fun getDecline(@Path("studentUUID") studentUUID: String): Observable<List<FormModel.LicenseGrade>>

    @GET("setTime/getLicenseTime")
    fun getLicenseTime(): Observable<FormModel.ApplyTime>

    companion object {
        fun create(): FormApplyApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(FormApplyApiService::class.java)
        }
    }
}