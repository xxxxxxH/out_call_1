package net.basicmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_activity_set.*
import kotlinx.android.synthetic.main.layout_title_bar.*
import net.utils.ResourceManager

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_set)
        initView()
    }


    private fun initView() {
        titleTv.text="setting"
        var defaultStatus1 = MMKV.defaultMMKV()!!.decodeBool("statusV", false)
        val default1 = ResourceManager.get()
            .resId2String(this, if (defaultStatus1) R.mipmap.toggle_on else R.mipmap.toggle_off)
        var defaultStatus2 = MMKV.defaultMMKV()!!.decodeBool("statusF", false)
        val default2 = ResourceManager.get()
            .resId2String(this, if (defaultStatus2) R.mipmap.toggle_on else R.mipmap.toggle_off)
        Glide.with(this).load(default1).into(vibrateToggle)
        Glide.with(this).load(default2).into(flagToggle)
        vibrateToggle.setOnClickListener {
            Glide.with(this).load(if (defaultStatus1) R.mipmap.toggle_off else R.mipmap.toggle_on)
                .into(vibrateToggle)
            MMKV.defaultMMKV()!!.encode("statusV", !defaultStatus1)
            defaultStatus1 = !defaultStatus1
        }
        flagToggle.setOnClickListener {
            Glide.with(this).load(if (defaultStatus2) R.mipmap.toggle_off else R.mipmap.toggle_on)
                .into(flagToggle)
            MMKV.defaultMMKV()!!.encode("statusF", !defaultStatus2)
            defaultStatus2 = !defaultStatus2
        }
        backIcon.setOnClickListener {
            finish()
        }
    }
}