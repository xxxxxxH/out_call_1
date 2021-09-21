package net.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings.Secure

class SettingManager {
    companion object{
        private var instance:SettingManager?=null
        get() {
            field?.let {

            }?:run {
               field = SettingManager()
            }
            return field
        }
        @Synchronized
        fun get():SettingManager{
            return instance!!
        }
    }

}