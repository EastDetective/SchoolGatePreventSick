package com.example.cs.pushpull.school.previous.response

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.toISO8601UTC
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.ResponseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*

class ResponseNewFragment : Fragment() {

    companion object {
        const val TAG = "NewRes"
    }

    // UI Nodes
    private lateinit var radioPublic: RadioButton
    private lateinit var radioPrivate: RadioButton
    private lateinit var input: EditText
    private lateinit var sendBtn: Button

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = "新增反應"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response_new, container, false).apply {

            // Node Binding
            radioPublic = findViewById(R.id.newResponse_publicRadio)
            radioPrivate = findViewById(R.id.newResponse_privateRadio)
            input = findViewById(R.id.newResponse_input)
            sendBtn = findViewById(R.id.newResponse_btn)

            input.setSingleLine(false)

            arguments?.run {
                sendBtn.setOnClickListener {
                    postResponse(ResponseModel.ResponsePost(
                        if (radioPublic.isChecked) 0 else 1,
                        input.text.toString(),
                        Calendar.getInstance().time.toISO8601UTC(),
                        getString("courseId")!!,
                        (activity as PushPull).studentUUID!!
                    ))
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun postResponse(response: ResponseModel.ResponsePost) {
        disposable = courseApiService.postResponse(response)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onComplete = {
                    Log.d(TAG, "Send Complete!")
                    Toast.makeText(context, "新增課程反應成功", Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                },
                onError = {
                    if (it is HttpException) {
                        when (it.message!!.split(" ")[1].toInt()) {
                            600 -> {
                                Toast.makeText(context, R.string.error_code_E501, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else {
                        Log.e(TAG, it.message)
                        Toast.makeText(context, "新增課程反應失敗，請稍後再試", Toast.LENGTH_LONG).show()
                    }
                }
            )
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}