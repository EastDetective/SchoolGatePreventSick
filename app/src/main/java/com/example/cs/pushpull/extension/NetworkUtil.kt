package com.example.cs.pushpull.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class NetworkUtil {

    companion object {
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2
        const val TYPE_NOT_CONNECTED = 0
        const val NETWORK_STATUS_NOT_CONNECTED = 0
        const val NETWORK_STATUS_WIFI = 1
        const val NETWORK_STATUS_MOBILE = 2

        fun getConnectivityStatus(context: Context): Int {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo

            activeNetwork?.run {
                if (type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
                if (type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
            }

            return TYPE_NOT_CONNECTED
        }

        fun getConnectivityStatusString(context: Context): Int {
            val conn = getConnectivityStatus(context)
            var status = 0
            when (conn) {
                TYPE_WIFI -> status = NETWORK_STATUS_WIFI
                TYPE_MOBILE -> status = NETWORK_STATUS_MOBILE
                TYPE_NOT_CONNECTED -> status = NETWORK_STATUS_NOT_CONNECTED
            }

            return status
        }
    }
}

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val status = NetworkUtil.getConnectivityStatusString(context!!)
        Log.e("Receiver", "Sulod sa network reciever")
        if ("android.net.conn.CONNECTIVITY_CHANGE" == intent!!.action) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                Log.d("Receiver", "NOT_CONNECTED")
            } else {
                Log.d("Receiver", "CONNECT")
            }
        }
    }

}