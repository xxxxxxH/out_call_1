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
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.CAMERA",
        "android.permission.READ_CALL_LOG"
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
}