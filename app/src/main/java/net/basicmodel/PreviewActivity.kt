package net.basicmodel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_activity_preview.*
import net.entity.ResourceEntity
import net.event.MessageEvent
import net.utils.ResourceManager
import org.greenrobot.eventbus.EventBus

class PreviewActivity : AppCompatActivity() {

    var videoPath :String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_preview)
        initData()
        initView()
    }

    private fun initData() {
        val i = intent
        videoPath = i.getStringExtra("path")
        video.setVideoPath(videoPath)
        video.start()
        video.setOnPreparedListener {
            it.isLooping = true
            it.setVolume(0f, 0f)
        }
    }

    private fun initView(){
        applybtn.setOnClickListener {
            Toast.makeText(this@PreviewActivity, "Applied...", Toast.LENGTH_SHORT).show()
            MMKV.defaultMMKV()!!.encode("video",videoPath)
            EventBus.getDefault().post(MessageEvent(videoPath))
            finish()
        }
    }
}