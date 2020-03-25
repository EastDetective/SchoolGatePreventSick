package com.example.cs.pushpull

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import com.example.cs.pushpull.extension.NetworkUtil
import com.example.cs.pushpull.game.LuckyDayStartFragment
import com.example.cs.pushpull.notification.NotificationFragment
import com.example.cs.pushpull.personal.PersonalFragment
import com.example.cs.pushpull.personal.model.ProfileModel
import com.example.cs.pushpull.school.SchoolFragment
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pushpull.*
import retrofit2.HttpException
import java.io.*

class PushPull : AppCompatActivity() {

    private companion object {
        const val LOGIN_REQUEST_CODE = 0
        const val TAG = "pushpull"

        const val EXTERNAL_STORAGE_READ_REQUEST_CODE = 1
    }

    private var mainView: FrameLayout? = null

    var studentName: String? = null
        private set
    var studentUUID: String? = null
        private set
    var studentID: String? = null
        private set
    private val fileName = "user.dat"

    // TODO : TEST
    var personalData: ProfileModel.Full? = null

    private val settings_pushpull = InternetObservingSettings.builder()
        .initialInterval(3000)
        .interval(3000)
        .host(BuildConfig.PUSHPULL_SERVER)
        .port(80)
        .timeout(4000)
//        .httpResponse(httpResponse)
        .errorHandler { exception, s ->
            Log.e("Error", exception.message)
            Log.e("Error", s)
        }
        .strategy(SocketInternetObservingStrategy())
        .build()

    private val settings_google = InternetObservingSettings.builder()
        .initialInterval(3000)
        .interval(3000)
        .host(BuildConfig.GOOGLE_DOMAIN)
        .port(80)
        .timeout(4000)
//        .httpResponse(httpResponse)
        .errorHandler { exception, s ->
            Log.e("Error", exception.message)
            Log.e("Error", s)
        }
        .strategy(SocketInternetObservingStrategy())
        .build()

    var googleStatus = true
    var serverStatus = true

    // Api Service for Course
    private val loginApiService by lazy {
        LoginApiService.create()
    }
    private var disposable: Disposable? = null

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = NetworkUtil.getConnectivityStatusString(context!!)

            Log.e("Receiver", "Connectivity Changed.")
            if ("android.net.conn.CONNECTIVITY_CHANGE" == intent!!.action) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.d("Receiver", "NOT_CONNECTED")
                    googleStatus = false
                    serverStatus = false
                    mainView?.run {
                        Snackbar.make(this, "連線不穩，請檢查網路狀態", Snackbar.LENGTH_LONG).show()
                    }
                }
                else {
                    Log.d("Receiver", "CONNECT")

                }
            }
        }
    }

    /*private val receiver = ConnectivityReceiver()

    private val settings = InternetObservingSettings.builder()
        .initialInterval(3000)
        .interval(3000)
        .host(BuildConfig.API_SERVER)
        .port(8080)
        .timeout(4000)
//        .httpResponse(httpResponse)
        .errorHandler { exception, s ->
            Log.e("Error", exception.message)
            Log.e("Error", s)
        }
        .strategy(SocketInternetObservingStrategy())
        .build()*/


//    public fun isNetworkOnline():Boolean {
//        var runtime:Runtime = Runtime.getRuntime()
//        try {
//            var ipProcess:Process = runtime.exec("ping -c 1 http://163.21.117.108:80")
//            var exitValue:Int = ipProcess.waitFor()
//            Log.d("ServerNetwork", "connect")
//            return exitValue == 0
//        } catch (e:InterruptedException) {
//            Log.d("ServerNetwork", "not connect")
//            e.printStackTrace()
//        }
//
//        return false
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pushpull)

        ReactiveNetwork
            .observeInternetConnectivity(settings_google)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                Log.d("ServerNetwork", isConnectedToInternet.toString())
                googleStatus = isConnectedToInternet

                mainView?.run {

                    // 判斷 googleStatus, serverStatus
                    if (googleStatus == true && serverStatus == false) {
                        Snackbar.make(this, "伺服器維修中", Snackbar.LENGTH_LONG).show()
                    } else if (googleStatus == false && serverStatus == true) {
//                            Snackbar.make(this, "Connectivity Available", Snackbar.LENGTH_LONG).show()
                    } else if (googleStatus == false && serverStatus == false) {
                        Snackbar.make(this, "請確認有連上網路", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        ReactiveNetwork
            .observeInternetConnectivity(settings_pushpull)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                Log.d("ServerNetwork", isConnectedToInternet.toString())
                serverStatus = isConnectedToInternet

                mainView?.run {

                    // 判斷 googleStatus, serverStatus
                    if (googleStatus == true && serverStatus == false) {
                        Snackbar.make(this, "伺服器維修中", Snackbar.LENGTH_LONG).show()
                    } else if (googleStatus == false && serverStatus == true) {
//                            Snackbar.make(this, "Connectivity Available", Snackbar.LENGTH_LONG).show()
                    } else if (googleStatus == false && serverStatus == false) {
                        Snackbar.make(this, "請確認有連上網路", Snackbar.LENGTH_LONG).show()
                    }
                }
            }

        // Remove Elevation
        supportActionBar!!.elevation = 0.toFloat()
        mainView = findViewById(R.id.push_pull_fragment_holder)


        // Local Data Check
        val fileInputStream: FileInputStream?
        val stringBuilder = StringBuilder()
        try {
            fileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }

            stringBuilder.toString().split(" ").run {

                // TODO : Just simply take data
                studentName = this[2]
                studentID = this[0]
                studentUUID = this[3]

                // TODO : Debug
                Log.d(TAG, studentUUID)

                // Identify the User
                disposable =
                    loginApiService.login(LoginCheck(this[0], this[1]))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onComplete = {
                            },
                            onError = {
                                if (it is HttpException) {
                                    when (it.message!!.split(" ")[1].toInt()) {
                                        604 -> {
                                            Toast.makeText(
                                                this@PushPull,
                                                "此用戶不存在",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this@PushPull, "未知問題，請重新登入", Toast.LENGTH_LONG)
                                        .show()
                                    Log.e(TAG, it.message)
                                }

                                // Launch Login Activity
                                startActivityForResult(
                                    Intent(this@PushPull, Login::class.java),
                                    LOGIN_REQUEST_CODE
                                )
                            }
                        )

                // Home Fragment
                supportFragmentManager.beginTransaction()
                    .add(R.id.push_pull_fragment_holder, SchoolFragment())
                    .commit()
            }
        } catch (e: FileNotFoundException) {

            // No User Data in Internal Storage
            Toast.makeText(this@PushPull, "本地端無資料，請登入", Toast.LENGTH_LONG).show()

            // Launch Login Activity
            startActivityForResult(Intent(this, Login::class.java), LOGIN_REQUEST_CODE)
        }

        // Bottom Navigation
        pushPull_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_personal -> {
                    createFragment(PersonalFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_school -> {
                    createFragment(SchoolFragment())

                    // Make Sure This will be Root one
                    supportFragmentManager.popBackStackImmediate(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )

                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_game -> {
                    createFragment(LuckyDayStartFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notification -> {
                    createFragment(NotificationFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    // Button on ActionBar
    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    @SuppressLint("PrivateResource")
    private fun createFragment(frag: Fragment) {
        if (supportFragmentManager.findFragmentById(R.id.push_pull_fragment_holder).toString().substring(
                0,
                supportFragmentManager.findFragmentById(R.id.push_pull_fragment_holder).toString().indexOf(
                    "{"
                )
            ) != frag.toString().substring(0, frag.toString().indexOf("{"))
        ) {
            supportFragmentManager.beginTransaction().apply {
                if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                setCustomAnimations(
                    R.anim.abc_fade_in,
                    R.anim.abc_fade_out,
                    R.anim.abc_fade_in,
                    R.anim.abc_fade_out
                )
                replace(R.id.push_pull_fragment_holder, frag)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE) {

            // Restore Known Data
            studentID = data?.getStringExtra("account")
            val password = data?.getStringExtra("password")
            studentName = data?.getStringExtra("studentName")
            studentUUID = data?.getStringExtra("UUID")

            // Write into Internal Storage
            val fileOutputStream: FileOutputStream
            try {
                fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
                fileOutputStream.write("$studentID $password $studentName $studentUUID".toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Home Fragment
            supportFragmentManager.beginTransaction()
                .add(R.id.push_pull_fragment_holder, SchoolFragment())
                .commit()
        }
    }

    fun makeExternalStorageRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_READ_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            EXTERNAL_STORAGE_READ_REQUEST_CODE -> {
                // If Request is cancelled, the Result arrays are Empty.
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Get permission Succeed

                } else Toast.makeText(this, "Permission Needed", Toast.LENGTH_SHORT).show()
            }
        }
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
}

