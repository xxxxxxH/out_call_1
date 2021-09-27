package net.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import net.DisconectClass
import net.RecivedClass
import net.basicmodel.R
import net.utils.ContractsUtil
import net.utils.ResourceManager
import java.util.*


class PhoneService : Service() {

    var phoneStateListener: PhoneStateListener? = null
    var telephonyManager: TelephonyManager? = null
    var wm: WindowManager? = null
    var audioManager: AudioManager? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()

        wm = getSystemService("window") as WindowManager
        Log.i("xxxxxxH", "wm = $wm")
        audioManager = getSystemService("audio") as AudioManager
        Log.i("xxxxxxH", "audioManager = $audioManager")

        telephonyManager = getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        Log.i("xxxxxxH", "telephonyManager = $telephonyManager")
        phoneStateListener = object : PhoneStateListener() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onCallStateChanged(state: Int, number: String) {
                super.onCallStateChanged(state, number)
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    showMyScreen(number)
                }
            }
        }
        Log.i("xxxxxxH", "phoneStateListener = $phoneStateListener")
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        Log.i("xxxxxxH", "telephonyManager = $telephonyManager")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showMyScreen(num: String) {
        var LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= 26) {
            2038
        } else {
            2002
        }
        val view = LayoutInflater.from(this).inflate(R.layout.layout_my_screen, null)
        view.findViewById<TextView>(R.id.callerName).text = ContractsUtil.getContactName(this, num)
        view.findViewById<TextView>(R.id.callerNumber).text = num
        val videoPath = MMKV.defaultMMKV()!!.decodeString("video", "")
        val video = view.findViewById<VideoView>(R.id.callVideo)
        if (!TextUtils.isEmpty(videoPath)) {
            video.setVideoPath(videoPath)
            video.start()
            video.setOnPreparedListener {
                it.isLooping = true
                it.setVolume(0f, 0f)
            }
        }

        val aRes = ResourceManager.get().getAnswerRes(this, R.mipmap::class.java, "mipmap")
        val rRes = ResourceManager.get().getRejectRes(this, R.mipmap::class.java, "mipmap")
        waitingData(aRes, 7)
        waitingData(rRes, 7)
        val themeData = ResourceManager.get().mergeRes(aRes, rRes)
        val index = MMKV.defaultMMKV()!!.decodeInt("theme", -1)

        val answer = view.findViewById<ImageView>(R.id.answerCall)
        val disconect = view.findViewById<ImageView>(R.id.disconectCall)

        if (index != -1) {
            val entity = themeData[index]
            Glide.with(this).load(ResourceManager.get().resId2String(this, entity.id))
                .into(answer)
            Glide.with(this).load(ResourceManager.get().resId2String(this, entity.id2))
                .into(disconect)
        }

        disconect.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 26) {
                DisconectClass(this).rejectCall(this)
            } else {
                DisconectClass(this).disconnectCall()
            }
            wm!!.removeViewImmediate(view)
        }
        answer.setOnClickListener {
            RecivedClass(this).sendHeadsetHookLollipop()
            wm!!.removeViewImmediate(view)
        }
        audioManager!!.setStreamVolume(3, 0, 0)
        wm!!.addView(view, WindowManager.LayoutParams(-1, -1, LAYOUT_FLAG, 19399552, -3))
    }

    private fun waitingData(data: ArrayList<*>, max: Int) {
        while (data.size < max) {
            Thread.sleep(100)
        }
    }
}