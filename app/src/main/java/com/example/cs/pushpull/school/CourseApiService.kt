package com.example.cs.pushpull.school

import com.example.cs.pushpull.BuildConfig
import com.example.cs.pushpull.school.model.ResponseModel
import com.example.cs.pushpull.school.model.CourseModel
import com.example.cs.pushpull.school.model.RollCallModel
import com.example.cs.pushpull.school.model.SurveyModel
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CourseApiService {

    //// Banner in SchoolFragment
    @GET("banner")
    fun getBannerContent(): Observable<List<CourseModel.Banner>>

    //// Courses
    // Get information of all courses
    @GET("course/getCourseAll/{studentId}")
    fun getCourseList(@Path("studentId") studentId: String): Observable<List<CourseModel.Course>>

    //// Favorite
    // Get All Courses on Favorite List
    @GET("course/getMyFavoriteCourses/{studentId}")
    fun getFavoriteList(@Path("studentId") studentId: String): Observable<List<CourseModel.Course>>

    //// Response
    // Get Response for course
    @GET("course/evaluationPublic/{courseID}")
    fun getPublicResponse(@Path("courseID") courseID: String): Observable<List<ResponseModel.Response>>

    @GET("course/{courseID}/evaluationPrivate/{studentID}")
    fun getPrivateResponse(@Path("courseID") courseID: String, @Path("studentID") studentID: String): Observable<List<ResponseModel.Response>>

    // Post Response
    @Headers("Content-Type:application/json")
    @POST("courseEvaluation")
    fun postResponse(@Body response: ResponseModel.ResponsePost): Completable

    //// CourseList
    // Button Function for Course ListItem
    @Headers("Content-Type:application/json")
    @POST("student/{studentId}/voteCourses/{courseId}")
    fun voteCourse(@Path("studentId") studentId: String, @Path("courseId") courseId: String, @Body courseWeek: CourseModel.CoursePost): Completable

    @DELETE("student/{studentId}/voteCourses/{courseId}")
    fun disVoteCourse(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Completable

    @POST("student/{studentId}/applyCourses/{courseId}")
    fun applyCourse(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Observable<CourseModel.Apply>

    @GET("student/{studentId}/applyCourses/{courseId}")
    fun applyCourseGet(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Observable<CourseModel.Apply>

    @DELETE("student/{studentId}/applyCourses/{courseId}")
    fun disApplyCourse(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Completable

    // Set As Favorite Course List
    @POST("student/{studentId}/favoriteCourses/{courseId}")
    fun setFavorite(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Completable

    // Remove from Favorite Course List
    @DELETE("student/{studentId}/favoriteCourses/{courseId}")
    fun setNonFavorite(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Completable

    // Set As Favorite Course List (For Those about Voting)
    @POST("student/{studentId}/favoriteRecCourses/{courseId}")
    fun setRecFavorite(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Completable

    // Remove from Favorite Course List (For Those about Voting)
    @DELETE("student/{studentId}/favoriteRecCourses/{courseId}")
    fun setRecNonFavorite(@Path("studentId") studentId: String, @Path("courseId") courseId: String): Completable

    // My Course Page
    @GET("course/getMyCourses/{studentId}")
    fun getMyCourses(@Path("studentId") studentId: String): Observable<List<CourseModel.Course>>

    @GET("course/getMyRecommendCourses/{studentId}")
    fun getMyRecCourses(@Path("studentId") studentId: String): Observable<List<CourseModel.Course>>

    @GET("course/getMyVoteCourses/{studentId}")
    fun getMyVotedCourses(@Path("studentId") studentId: String): Observable<List<CourseModel.Course>>

    //// Satisfaction Survey
    @GET("courseSatisfaction/{courseId}")
    fun getSatisfactionSurveyQuestion(@Path("courseId") courseId: String): Observable<SurveyModel.SurveyQuestion>

    @Headers("Content-Type:application/json")
    @POST("courseSatisfactionAns")
    fun answerSatisfactionSurvey(@Body answer: SurveyModel.SurveyAnswer): Completable

    //// RollCall
    @GET("student/{studentId}/inCourses")
    fun getAllInCourse(@Path("studentId") studentId: String): Observable<List<RollCallModel.CourseSimple>>

    @Headers("Content-Type:application/json")
    @PUT("course/{courseId}/rollCall/{studentId}")
    fun putVerification(@Path("courseId") courseId: String, @Path("studentId") studentId: String, @Body Verification: RollCallModel.Verification): Observable<RollCallModel.Verification>

    @Headers("Content-Type:application/json")
    @POST("rcAccount/{studentId}")
    fun postRollCallAccount(@Path("studentId") studentId: String, @Body postAccount: RollCallModel.Account): Observable<RollCallModel.RollCallLoginResponse>

    @Headers("Content-Type:application/json")
    @POST("rcAccount")
    fun postRollCall(@Body postAccount: RollCallModel.Account): Observable<RollCallModel.RollCallLoginResponse>

    // For Teacher
    @Headers("Content-Type:application/json")
    @POST("rollcall")
    fun addARollCall(@Body addRollCall: RollCallModel.AddRollCall): Completable

    companion object {
        fun create(): CourseApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_SERVER)
                .build()
            return retrofit.create(CourseApiService::class.java)
        }
    }
}