package com.example.cs.pushpull.school.rollcall.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cs.pushpull.R
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.RollCallModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class ScanLoginFragment : Fragment() {

    companion object {
        const val TAG = "ScanLogin"
    }

    // UI Nodes
    private lateinit var loginBtn: Button
    private lateinit var accountID: EditText
    private lateinit var accountPassword: EditText
    private lateinit var courseID: EditText

    private var rcAccountID: String? = null

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_login, container, false).apply {

            val enter: String = (activity as Scan).from

            accountID = findViewById(R.id.scanner_account)
            accountPassword = findViewById(R.id.scanner_password)
            courseID = findViewById(R.id.scanner_courseid)

            // Node Binding
            loginBtn = findViewById(R.id.scan_login_btn)

            loginBtn.setOnClickListener {
                when {
                    accountID.text.toString() == "" -> Toast.makeText(context, "請輸入帳號", Toast.LENGTH_LONG).show()
                    accountPassword.text.toString() == "" -> Toast.makeText(context, "請輸入密碼", Toast.LENGTH_LONG).show()
                    courseID.text.toString() == "" -> Toast.makeText(context, "請輸入課程代碼", Toast.LENGTH_LONG).show()
                }
                if (accountID.text.toString().isNotEmpty() && accountPassword.text.toString().isNotEmpty() && courseID.text.toString().isNotEmpty()) {
                    if (enter == "LoginPage") {
                        postNoStdUUID(
                            RollCallModel.Account(
                                accountID.text.toString(),
                                accountPassword.text.toString(),
                                courseID.text.toString()
                            )
                        )
                    } else {
                        postResponse(
                            RollCallModel.Account(
                                accountID.text.toString(),
                                accountPassword.text.toString(),
                                courseID.text.toString()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun postResponse(post: RollCallModel.Account) {
        disposable =
            courseApiService.postRollCallAccount((activity as Scan).stdUUID, post)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        rcAccountID = it.rcaccountID
                    },
                    onComplete = {
                        Log.d("Complete", "Post Succeed!")
                        goScan()
                    },
                    onError = {
                        if (it is HttpException) {
                            when (it.message!!.split(" ")[1].toInt()) {
                                604 -> {
                                    Toast.makeText(context, "帳號不存在", Toast.LENGTH_LONG).show()
                                }
                                603 -> {
                                    Toast.makeText(context, "密碼錯誤", Toast.LENGTH_LONG).show()
                                }
                                609 -> {
                                    Toast.makeText(context, "無點名權限", Toast.LENGTH_LONG).show()
                                }
                                610 -> {
                                    Toast.makeText(context, "課程代碼錯誤", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Log.e("Error", it.message)
                            Toast.makeText(context, "登入失敗 請稍後再試", Toast.LENGTH_LONG).show()
                        }
                    }
                )
    }

    private fun postNoStdUUID(post: RollCallModel.Account) {
        disposable =
            courseApiService.postRollCall(post)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        rcAccountID = it.rcaccountID
                    },
                    onComplete = {
                        Log.d("Complete", "Post Succeed!")
                        goScan()
                    },
                    onError = {
                        if (it is HttpException) {
                            when (it.message!!.split(" ")[1].toInt()) {
                                604 -> {
                                    Toast.makeText(context, "帳號不存在", Toast.LENGTH_LONG).show()
                                }
                                603 -> {
                                    Toast.makeText(context, "密碼錯誤", Toast.LENGTH_LONG).show()
                                }
                                609 -> {
                                    Toast.makeText(context, "無點名權限", Toast.LENGTH_LONG).show()
                                }
                                610 -> {
                                    Toast.makeText(context, "課程代碼錯誤", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Log.e("Error", it.message)
                            Toast.makeText(context, "登入失敗 請稍後再試", Toast.LENGTH_LONG).show()
                        }
                    }
                )
    }

    private fun goScan() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            (activity as Scan).makeCameraRequest(Manifest.permission.CAMERA)
        } else {
            // Permission has already been granted
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.scan_fragment_holder, ScanFragment().apply {
                    arguments = Bundle().also {
                        it.putString("rcaccountID", rcAccountID)
                        it.putString("courseCode", courseID.text.toString())
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }
}
