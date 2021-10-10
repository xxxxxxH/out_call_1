package net.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import com.android.internal.telephony.ITelephony
import net.service.MyNotificationListenerService
import java.lang.reflect.Method

class PhoneManager {
    companion object {
        private var i: PhoneManager? = null
            get() {
                field ?: run {
                    field = PhoneManager()
                }
                return field
            }

        @Synchronized
        fun get(): PhoneManager {
            return i!!
        }
    }


    /**
     * 接听电话
     */
    fun answer(context: Context) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                val telecomManager =
                    context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ANSWER_PHONE_CALLS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                telecomManager.acceptRingingCall()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                finalAnswer(context)
            }
            else -> {
                try {
                    val method: Method = Class.forName("android.os.ServiceManager")
                        .getMethod("getService", String::class.java)
                    val binder = method.invoke(null, Context.TELEPHONY_SERVICE) as IBinder
                    val telephony = ITelephony.Stub.asInterface(binder)
                    telephony.answerRingingCall()
                } catch (e: Exception) {
                    finalAnswer(context)
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun finalAnswer(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val mediaSessionManager =
                    context.getSystemService("media_session") as MediaSessionManager
                val activeSessions = mediaSessionManager.getActiveSessions(
                    ComponentName(
                        context,
                        MyNotificationListenerService::class.java
                    )
                ) as List<MediaController>
                if (activeSessions.isNotEmpty()) {
                    for (mediaController in activeSessions) {
                        if ("com.android.server.telecom" == mediaController.packageName) {
                            mediaController.dispatchMediaButtonEvent(KeyEvent(0, 79))
                            mediaController.dispatchMediaButtonEvent(KeyEvent(1, 79))
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            answerPhoneAidl(context)
        }
    }

    private fun answerPhoneAidl(context: Context) {
        try {
            val keyEvent = KeyEvent(0, 79)
            val keyEvent2 = KeyEvent(1, 79)
            if (Build.VERSION.SDK_INT >= 19) {
                @SuppressLint("WrongConstant") val audioManager =
                    context.getSystemService("audio") as AudioManager
                audioManager.dispatchMediaKeyEvent(keyEvent)
                audioManager.dispatchMediaKeyEvent(keyEvent2)
            }
        } catch (ex: java.lang.Exception) {
            val intent = Intent("android.intent.action.MEDIA_BUTTON")
            intent.putExtra("android.intent.extra.KEY_EVENT", KeyEvent(0, 79) as Parcelable)
            context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED")
            val intent2 = Intent("android.intent.action.MEDIA_BUTTON")
            intent2.putExtra("android.intent.extra.KEY_EVENT", KeyEvent(1, 79) as Parcelable)
            context.sendOrderedBroadcast(intent2, "android.permission.CALL_PRIVILEGED")
        }
    }

    fun disconnect(context: Context) {

        try {
            @SuppressLint("WrongConstant")
            val telephonyManager =
                context.getSystemService("phone") as TelephonyManager
            val method = Class.forName(telephonyManager.javaClass.name)
                .getDeclaredMethod("getITelephony", *arrayOfNulls(0))
            method.isAccessible = true
            (method.invoke(telephonyManager, *arrayOfNulls(0)) as ITelephony).endCall()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}