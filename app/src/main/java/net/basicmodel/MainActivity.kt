package net.basicmodel

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weeboos.permissionlib.PermissionRequest
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var isNotifi = false
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

    private fun topShow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(this)){
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(intent)
            }
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
            startActivity(Intent(this,SettingActivity::class.java))
        }
    }

    private fun startSelectActivity(type: String) {
        val i = Intent(this, SelectActivity::class.java)
        i.putExtra("type", type)
        startActivity(i)
    }


}