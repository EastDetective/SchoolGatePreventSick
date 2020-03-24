package com.example.cs.pushpull.school.previous.response

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
import com.example.cs.pushpull.school.model.ResponseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ResponseContentFragment : Fragment() {

    companion object {
        const val TAG = "ResContent"
    }

    // UI Node
    private lateinit var responseList: ListView

    // Raw Data Container
    private var response: List<ResponseModel.Response> = listOf()

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
        return inflater.inflate(R.layout.fragment_response_content, container, false).apply {

            // Node Binding
            this?.let { responseList = findViewById(R.id.courseResponse_list) }

            // Get Data and then Display it
            val courseId = arguments?.getString("courseId")
            courseId?.run {
                if (arguments?.get("TabNow") == "公開") {
                    getResponsePublic(courseId)
                } else {
                    getResponsePrivate(courseId)
                }
            }
        }
    }

    private fun getResponsePublic(courseID: String) {

        disposable = courseApiService.getPublicResponse(courseID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            // Be sure do this after finishing subscribe
            .doAfterTerminate {

                // Setup List Adapter
                responseList.adapter = ResponseListAdapter(
                    context!!,
                    response,
                    courseApiService,
                    disposable,
                    courseID,
                    activity!!.supportFragmentManager
                )
            }
            .subscribeBy(
                onNext = {
                    response = it.filter { get ->
                        get.state == 0
                    }
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

    private fun getResponsePrivate(courseID: String) {

        disposable = courseApiService.getPrivateResponse(courseID, (activity as PushPull).studentUUID!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            // Be sure do this after finishing subscribe
            .doAfterTerminate {

                // Setup List Adapter
                responseList.adapter = ResponseListAdapter(
                    context!!,
                    response,
                    courseApiService,
                    disposable,
                    courseID,
                    activity!!.supportFragmentManager
                )
            }
            .subscribeBy(
                onNext = {
                    response = it.filter { get ->
                        get.state == 1
                    }
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

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        Log.d(TAG, "Disposed")
    }
}