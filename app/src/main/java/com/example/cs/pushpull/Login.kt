package com.example.cs.pushpull

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.*
import com.example.cs.pushpull.extension.NetworkUtil
import com.example.cs.pushpull.school.rollcall.scan.Scan
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.HttpException
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class Login : AppCompatActivity() {

    companion object {
        const val TAG = "Login"
    }

    // UI Nodes
    private lateinit var logoToScan: ImageView
    private lateinit var accountInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var rule: TextView

    // Data Receiver
    private var loginData: LoginSuccess? = null

    // Api Service for Course
    private val loginApiService by lazy {
        LoginApiService.create()
    }
    private var disposable: Disposable? = null

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = NetworkUtil.getConnectivityStatusString(context!!)
            Log.e("Receiver", "Sulod sa network reciever")
            if ("android.net.conn.CONNECTIVITY_CHANGE" == intent!!.action) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.d("Receiver", "NOT_CONNECTED")
                    login_main_view.run {
                        Snackbar.make(this, "連線不穩，請檢查網路狀態", Snackbar.LENGTH_LONG).show()
                    }
                }
//                else {
//                    Log.d("Receiver", "CONNECT")
//                    mainView?.run {
//                        Snackbar.make(this, "Connectivity Available", Snackbar.LENGTH_LONG).show()
//                    }
//                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Node Binding
        logoToScan = findViewById(R.id.logo)
        accountInput = findViewById(R.id.login_account)
        passwordInput = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        checkBox = findViewById(R.id.login_check_box)

        rule = findViewById(R.id.login_rule)
        rule.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        rule.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://163.21.117.108/appPrivacy")))
        }

        // Force Single Line
        accountInput.setSingleLine(true)

        logoToScan.setOnTouchListener(object : View.OnTouchListener {
            val handler = Handler()

            var numberOfTaps = 0
            var lastTapTimeMs: Long = 0
            var touchDownMs: Long = 0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> touchDownMs = System.currentTimeMillis()
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacksAndMessages(null)

                        // Not a Tap
                        if (System.currentTimeMillis() - touchDownMs > ViewConfiguration.getTapTimeout()) {
                            numberOfTaps = 0
                            lastTapTimeMs = 0
                        }

                        // Check Single Tap or Not
                        if (numberOfTaps > 0 && System.currentTimeMillis() - lastTapTimeMs < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1
                        } else {
                            numberOfTaps = 1
                        }

                        lastTapTimeMs = System.currentTimeMillis()

                        // If View has been touched 5 times
                        if (numberOfTaps == 5) {
                            Log.d("Tap", "Five")
                            startActivity(Intent(this@Login, Scan::class.java).putExtra("From", "LoginPage"))
                        }
                    }
                }
                return true
            }
        })

        // Button OnClick Setting
        loginButton.setOnClickListener {
            if (!(accountInput.text.isNotEmpty() && passwordInput.text.isNotEmpty())) {
                Toast.makeText(baseContext, "請輸入帳號密碼", Toast.LENGTH_SHORT).show()
            } else {
                // When Account and Password are filled
                if (!checkBox.isChecked) {
                    Toast.makeText(baseContext, "請同意使用者規範", Toast.LENGTH_SHORT).show()
                } else {
                    // When checkBox is Checked
                    disposable =
                        loginApiService.login(LoginCheck(accountInput.text.toString(), passwordInput.text.toString()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                onNext = {
                                    loginData = it
                                },
                                onComplete = {
                                    loginData?.let {
                                        setResult(Activity.RESULT_OK, Intent().apply {
                                            putExtra("account", accountInput.text.toString())
                                            putExtra("password", passwordInput.text.toString())
                                            putExtra("studentName", it.studentName)
                                            putExtra("UUID", it.studentUUID)
                                        })
                                        FirebaseInstanceId.getInstance().instanceId
                                            .addOnCompleteListener(OnCompleteListener { task ->
                                                if (!task.isSuccessful) {
                                                    Log.w(TAG, "getInstanceId failed", task.exception)
                                                    return@OnCompleteListener
                                                }

                                                // Get new Instance ID token
                                                val token = task.result?.token
                                                putTokenToServer(token!!, loginData!!.studentUUID)
                                                // Log and toast
                                                Log.d(TAG, "Token : $token")
                                            })
                                    }
                                },
                                onError = {
                                    if (it is HttpException) {
                                        when (it.message!!.split(" ")[1].toInt()) {
                                            603 -> {
                                                Toast.makeText(this, getString(R.string.error_code_E504), Toast.LENGTH_SHORT).show()
                                            }
                                            604 -> {
                                                Toast.makeText(this, getString(R.string.error_code_E504), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Log.e(TAG, it.message)
                                    }
                                }
                            )

                }
            }
        }
    }

    private fun putTokenToServer(token: String, stdUUID: String) {
        disposable = loginApiService.putToken(stdUUID, token)
            .subscribeOn(Schedulers.io())
            .doAfterTerminate {
                // Force This finish last
                finish()
            }
            .subscribeBy(
                onComplete = {
                    Log.d(TAG, "Token Upload Complete!! token is $token")
                },
                onError = {
                    // TODO : Error Code Handling
                    Log.e(TAG, it.message)
                }
            )
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        exitProcess(-1)
    }

}
