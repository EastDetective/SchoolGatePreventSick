package com.example.cs.pushpull.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.notification.model.NotificationModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class NotificationContentFragment : Fragment() {

    // Api Service for Course
    private val notificationApiService by lazy {
        NotificationApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private var notice: List<NotificationModel.Notice> = listOf()

    private lateinit var normalNotificationList: ListView
    private lateinit var courseNotificationList: ListView
    private lateinit var studentNotificationList: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Decide to Inflate which layout for this fragment
        return when (arguments!!.getString("nTabNow")) {
            "一般公告" -> {
                inflater.inflate(R.layout.fragment_notification_content, container, false).apply {
                    normalNotificationList = findViewById(R.id.notification_list_content)
                    disposable =
                        notificationApiService.getNormalNotice()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                normalNotificationList.adapter = NotificationListAdapter(
                                    this.context,
                                    notice
                                )
                            }
                            .subscribeBy(
                                onNext = {
                                    notice = it
                                },
                                onError = {
                                    Log.e("Error", it.message)
                                }
                            )
                }
            }
            "課程群組" -> {
                inflater.inflate(R.layout.fragment_notification_course, container, false).apply {
                    courseNotificationList = findViewById(R.id.notification_list_course)
                    disposable =
                        notificationApiService.getCourseNotice((activity as PushPull).studentUUID!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                courseNotificationList.adapter = NotificationListAdapter(
                                    this.context,
                                    notice
                                )
                            }
                            .subscribeBy(
                                onNext = {
                                    notice = it
                                },
                                onError = {
                                    Log.e("Error", it.message)
                                }
                            )
                }
            }
            else -> {
                inflater.inflate(R.layout.fragment_notification_student, container, false).apply {
                    studentNotificationList = findViewById(R.id.notification_list_student)
                    disposable =
                        notificationApiService.getStudentNotice((activity as PushPull).studentUUID!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                studentNotificationList.adapter = NotificationListAdapter(
                                    this.context,
                                    notice
                                )
                            }
                            .subscribeBy(
                                onNext = {
                                    notice = it
                                },
                                onError = {
                                    Log.e("Error", it.message)
                                }
                            )
                }
            }
        }
    }
}