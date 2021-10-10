package net.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class PhoneReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
//        p0!!.startService(Intent(p0, PhoneService::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p0?.startForegroundService(Intent(p0, PhoneService::class.java))
        } else {
            p0?.startService(Intent(p0, PhoneService::class.java))
        }
        Log.i("xxxxxxH", "PhoneReceiver start")
    }
}