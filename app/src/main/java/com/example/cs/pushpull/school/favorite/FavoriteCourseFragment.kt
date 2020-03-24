package com.example.cs.pushpull.school.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.allcourse.AllCourseListAdapter
import com.example.cs.pushpull.school.model.CourseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class FavoriteCourseFragment : Fragment() {

    // UI Node
    private lateinit var favoriteList: ListView

    // Data Container
    private var favoriteCourses: List<CourseModel.Course> = listOf()

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Add a Back Icon on ActionBar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title on the ActionBar
        activity?.title = resources.getString(R.string.favorite_course)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_course, container, false).apply {

            // Node Binding
            this?.let { favoriteList = findViewById(R.id.favorite_course_list) }

            // Get Data and then Display it
            getFavoriteCourse((activity as PushPull).studentUUID!!)
        }
    }

    private fun getFavoriteCourse(studentId: String) {
        disposable = courseApiService.getFavoriteList(studentId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {

                // Setup List Adapter
                favoriteList.adapter = AllCourseListAdapter(
                    context!!,
                    favoriteCourses,
                    courseApiService,
                    disposable,
                    studentId,
                    true
                )
            }
            .subscribeBy(
                onNext = {
                    favoriteCourses = it
                },
                onError = {
                    Log.d("KETest", it.message)
                }
            )
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}