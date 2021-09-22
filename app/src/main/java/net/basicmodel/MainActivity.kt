package net.basicmodel

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.weeboos.permissionlib.PermissionRequest
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_my_screen.*
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
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
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
        if (requestCode == Activity.RESULT_OK) {
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
    }

    private val mPhoneStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (TextUtils.equals(action, "android.intent.action.PHONE_STATE")) {
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
        callerName.text = ContractsUtil.getContactName(this, num)
        callerNumber.text = num
        val videoPath = MMKV.defaultMMKV()!!.decodeString("video", "")
        if (!TextUtils.isEmpty(videoPath)) {
            callVideo.setVideoPath(videoPath)
            callVideo.start()
            callVideo.setOnPreparedListener {
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
        if (index != -1) {
            val entity = themeData[index]
            Glide.with(this).load(ResourceManager.get().resId2String(this, entity.id))
                .into(answerCall)
            Glide.with(this).load(ResourceManager.get().resId2String(this, entity.id2))
                .into(disconectCall)
        }

        disconectCall.setOnClickListener {
            if (VERSION.SDK_INT >= 26) {
                DisconectClass(this).rejectCall(this)
            } else {
                DisconectClass(this).disconnectCall()
            }
        }
        answerCall.setOnClickListener {
            RecivedClass(this).sendHeadsetHookLollipop()
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