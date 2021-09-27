package net.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PhoneReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0!!.startService(Intent(p0, PhoneService::class.java))
        Log.i("xxxxxxH", "PhoneReceiver start")
    }
}