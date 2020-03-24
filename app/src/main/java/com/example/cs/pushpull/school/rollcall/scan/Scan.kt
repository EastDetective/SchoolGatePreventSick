package com.example.cs.pushpull.school.rollcall.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import android.widget.Toast
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scan.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Scan : AppCompatActivity() {

    companion object {
        const val RECORD_REQUEST_CODE = 1
    }

    var from = ""
    var stdUUID = ""

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = NetworkUtil.getConnectivityStatusString(context!!)
            Log.e("Receiver", "Sulod sa network reciever")
            if ("android.net.conn.CONNECTIVITY_CHANGE" == intent!!.action) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.d("Receiver", "NOT_CONNECTED")
                    scan_fragment_holder.run {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        if (intent.extras.getString("From") != null){
            from = intent.extras.getString("From")
        }
        if(intent.extras.getString("sID") != null){
            stdUUID = intent.extras.getString("sID")
        }

        // Basic Fragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.scan_fragment_holder, ScanLoginFragment())
            commit()
        }
    }

    fun makeCameraRequest(vararg permissions: String) {
        if (permissions.isNotEmpty()) ActivityCompat.requestPermissions(this, permissions, RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                // If Request is cancelled, the Result arrays are Empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Get permission Succeed
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.scan_fragment_holder, ScanFragment())
                        .addToBackStack(null)
                        .commit()
                } else Toast.makeText(this, "Permission Needed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }
}