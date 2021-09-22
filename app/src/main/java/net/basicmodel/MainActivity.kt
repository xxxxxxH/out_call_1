package net.basicmodel

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.weeboos.permissionlib.PermissionRequest
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import net.DisconectClass
import net.RecivedClass
import net.utils.ContractsUtil
import net.utils.ResourceManager
import java.util.*


class MainActivity : AppCompatActivity() {
    var wm: WindowManager? = null
    private var audioManager: AudioManager? = null
    var permissions = arrayOf(
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.CAMERA"
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MMKV.initialize(this)
        requestPermission()
        send()
    }

    private fun topShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.packageName),
                )
                startActivityForResult(intent, 0)
            } else {
                notification()
            }
        }
    }

    private fun notification() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            val localIntent = Intent()
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0及以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                localIntent.data = Uri.fromParts("package", packageName, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0以上到8.0以下
                localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                localIntent.putExtra("app_package", packageName)
                localIntent.putExtra("app_uid", applicationInfo.uid)
            }
            startActivity(localIntent)
        }
    }

    private fun requestPermission() {
        PermissionRequest.getInstance().build(this)
            .requestPermission(object : PermissionRequest.PermissionListener {
                override fun permissionGranted() {
                    initView()
                    topShow()
                }

                override fun permissionDenied(permissions: ArrayList<String>?) {
                    finish()
                }

                override fun permissionNeverAsk(permissions: ArrayList<String>?) {
                    finish()
                }
            }, permissions)
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        videoBtn.setOnClickListener {
            startSelectActivity("video")
        }
        themeBtn.setOnClickListener {
            startSelectActivity("theme")
        }
        blockBtn.setOnClickListener {
            startActivity(Intent(this, BlockActivity::class.java))
        }
        settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        var mainStatus = MMKV.defaultMMKV()!!.decodeBool("main")
        Glide.with(this).load(if (mainStatus) R.mipmap.off else R.mipmap.on).into(switchBtn)
        switchBtn.setOnClickListener {
            Glide.with(this).load(if (mainStatus) R.mipmap.off else R.mipmap.on).into(switchBtn)
            MMKV.defaultMMKV()!!.encode("main", !mainStatus)
            mainStatus = !mainStatus
        }
        wm = getSystemService("window") as WindowManager?
        audioManager = getSystemService("audio") as AudioManager?
        val filter = IntentFilter()
        filter.addAction("android.intent.action.PHONE_STATE")
        filter.addAction("1")
        registerReceiver(mPhoneStateReceiver, filter)
    }

    private fun startSelectActivity(type: String) {
        val i = Intent(this, SelectActivity::class.java)
        i.putExtra("type", type)
        startActivity(i)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.packageName),
                )
                startActivityForResult(intent, 0)
            } else {
                notification()
            }
        }
    }

    private fun send() {
        send.setOnClickListener {
            val i = Intent()
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.action = "1"
            i.putExtra("incoming_number", "15680609620")
            this.sendBroadcast(i)
        }
    }

    private val mPhoneStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (TextUtils.equals(action, "android.intent.action.PHONE_STATE")) {
//            if (TextUtils.equals(action, "1")) {
                val state = intent.getStringExtra("state")
                val number = intent.getStringExtra("incoming_number")
                Log.i("xxxxxxH", "拦截到电话 state=$state number=$number")
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state, ignoreCase = true)) {
                showMyScreen(number!!)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showMyScreen(num: String) {
        var LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (VERSION.SDK_INT >= 26) {
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
            if (VERSION.SDK_INT >= 26) {
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