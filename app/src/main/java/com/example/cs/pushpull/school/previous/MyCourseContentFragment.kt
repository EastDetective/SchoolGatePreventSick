package com.example.cs.pushpull.school.previous

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.CourseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class MyCourseContentFragment : Fragment() {

    companion object {
        const val TAG = "MyCourse"
    }

    // UI Node
    private lateinit var myCourseListView: ListView

    // Raw Data Container
    private var myCourse: List<CourseModel.Course> = listOf()

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_course_content, container, false).apply {

            // Node Binding
            this?.let { myCourseListView = findViewById(R.id.my_course_list) }

            val studentId = (activity as PushPull).studentUUID!!

            when (val tabNow = arguments?.get("myCTabNow").toString()) {
                resources.getStringArray(R.array.my_course_tab)[0] -> {
                    disposable = courseApiService.getMyCourses(studentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        // Make sure do this after finishing subscribe
                        .doAfterTerminate {

                            // Setup List Adapter
                            myCourseListView.adapter = MyAttendedCourseListAdapter(
                                context!!,
                                myCourse,
                                courseApiService,
                                disposable,
                                studentId,
                                tabNow,
                                activity!!
                            )
                        }
                        .subscribeBy(
                            onNext = {
                                myCourse = it
                            },
                            onComplete = {
                                Log.d(TAG, "Getting Data Complete!!")
                            },
                            onError = {
                                // TODO : Error Handling?
                                Log.e(TAG, it.message)
                            }
                        )
                }
                resources.getStringArray(R.array.my_course_tab)[1] -> {
                    disposable = courseApiService.getMyRecCourses(studentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        // Be sure do this after finishing subscribe
                        .doAfterTerminate {

                            // Setup List Adapter
                            myCourseListView.adapter = MyAttendedCourseListAdapter(
                                context!!,
                                myCourse,
                                courseApiService,
                                disposable,
                                studentId,
                                tabNow,
                                activity!!
                            )
                        }
                        .subscribeBy(
                            onNext = {
                                myCourse = it
                            },
                            onComplete = {
                                Log.d(TAG, "Getting Data Complete!!")
                            },
                            onError = {
                                // TODO : Error Handling?
                                Log.e(TAG, it.message)
                            }
                        )
                }
                resources.getStringArray(R.array.my_course_tab)[2] -> {
                    disposable = courseApiService.getMyVotedCourses(studentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        // Be sure do this after finishing subscribe
                        .doAfterTerminate {

                            // Setup List Adapter
                            myCourseListView.adapter = MyAttendedCourseListAdapter(
                                context!!,
                                myCourse,
                                courseApiService,
                                disposable,
                                studentId,
                                tabNow,
                                activity!!
                            )
                        }
                        .subscribeBy(
                            onNext = {
                                myCourse = it
                            },
                            onComplete = {
                                Log.d(TAG, "Getting Data Complete!!")
                            },
                            onError = {
                                // TODO : Error Handling?
                                Log.e(TAG, it.message)
                            }
                        )
                }
            }
        }
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}