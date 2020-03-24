package com.example.cs.pushpull.school.allcourse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.CourseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class CourseContentFragment : Fragment() {

    companion object {
        const val TAG = "CourseAll"
    }

    // UI Node
    private lateinit var courseList: ListView

    // Raw Data Container
    private var course: List<CourseModel.Course> = listOf()

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_content, container, false).apply {

            // Node Binding
            this?.let { courseList = findViewById(R.id.course_list) }

            // Get Data and then Display it
            getAllCourses((activity as PushPull).studentUUID!!)
        }
    }

    private fun getAllCourses(studentId: String) {
        disposable = courseApiService.getCourseList(studentId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            // Be sure do this after finishing subscribe
            .doAfterTerminate {

                // Setup List Adapter
                courseList.adapter = AllCourseListAdapter(
                    context!!,
                    course,
                    courseApiService,
                    disposable,
                    studentId
                )
            }
            .subscribeBy(
                onNext = {
                    when (arguments?.get("TabNow")) {
                        resources.getStringArray(R.array.all_course_tab)[1] -> {
                            course = it.filter { get ->
                                get.status < 3
                            }
                        }
                        resources.getStringArray(R.array.all_course_tab)[2] -> {
                            course = it.filter { get ->
                                get.status in 3..7
                            }
                        }
                        resources.getStringArray(R.array.all_course_tab)[3] -> {
                            course = it.filter { get ->
                                get.status in 8..10
                            }
                        }
                        else -> {
                            course = it.filter { get ->
                                get.status < 11
                            }
                        }
                    }
                },
                onComplete = {
                    Log.d(TAG, "Getting Data Complete!!")
                },
                onError = {
                    Log.e(TAG, it.message)
                }
            )
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}

