package net.basicmodel

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.weeboos.permissionlib.PermissionRequest
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import net.event.MessageEvent
import net.service.PhoneReceiver
import net.service.PhoneService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class MainActivity : AppCompatActivity() {
    var wm: WindowManager? = null
    private var audioManager: AudioManager? = null
    var permissions = arrayOf(
        "android.permission.ANSWER_PHONE_CALLS",
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.ACTION_MANAGE_OVERLAY_PERMISSION",
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.CALL_PHONE",
        "android.permission.MODIFY_AUDIO_SETTINGS",
        "android.permission.VIBRATE",
        "android.permission.CAMERA",
        "android.permission.FLASHLIGHT",
        "android.permission.RECEIVE_BOOT_COMPLETED",
        "android.permission.INTERNET",
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.READ_CALL_LOG",
        "android.permission.MEDIA_CONTENT_CONTROL"
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MMKV.initialize(this)
        requestPermission()
        topShow()
    }

    private fun topShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.packageName),
                )
                startActivityForResult(intent, 0)
            }
        }
    }


    private fun requestPermission() {
        PermissionRequest.getInstance().build(this)
            .requestPermission(object : PermissionRequest.PermissionListener {
                override fun permissionGranted() {
                    initView()
                }

                override fun permissionDenied(permissions: ArrayList<String>?) {
                    finish()
                }

                override fun permissionNeverAsk(permissions: ArrayList<String>?) {
//                    finish()
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
    }



    private fun startSelectActivity(type: String) {
        val i = Intent(this, SelectActivity::class.java)
        i.putExtra("type", type)
        startActivity(i)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.packageName),
                )
                startActivityForResult(intent, 0)
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
}