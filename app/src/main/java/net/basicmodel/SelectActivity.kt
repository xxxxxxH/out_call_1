package net.basicmodel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_activity_select.*
import kotlinx.android.synthetic.main.layout_title_bar.*
import net.adapter.MyAdapter
import net.adapter.MyAdapter2
import net.entity.ResourceEntity
import net.entity.ResourceEntity2
import net.event.MessageEvent
import net.utils.GlideEngine
import net.utils.LoadingDialog
import net.utils.ResourceManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SelectActivity : AppCompatActivity() {

    var type: String? = null
    var data: ArrayList<ResourceEntity>? = null
    var myAdapter: MyAdapter? = null
    var myAdapter2: MyAdapter2? = null
    var dialog: LoadingDialog? = null
    var themeData: ArrayList<ResourceEntity2>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_select)
        EventBus.getDefault().register(this)
        init()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val key = event.getMessage()[0]
        setDefault(key, myAdapter!!)
    }

    private fun init() {
        val i = intent
        type = i.getStringExtra("type")
        when {
            TextUtils.equals(type, "video") -> {
                Choose.visibility = View.VISIBLE
                data = ResourceManager.get().getAllResource(this, R.raw::class.java, "raw")
                titleTv.text = "select video"
                Choose.setOnClickListener {
                    openGallery()
                }
                waitingData(data!!, 19)
                myAdapter = MyAdapter(
                    this,
                    R.layout.layout_item,
                    data
                )
                recycler.layoutManager = GridLayoutManager(this, 2)
                recycler.adapter = myAdapter
                setDefault(MMKV.defaultMMKV()!!.decodeString("video").toString(), myAdapter!!)
                myAdapter!!.setOnItemClickListener { adapter, view, position ->
                    startPreViewActivity(
                        ResourceManager.get().resId2String(
                            this@SelectActivity,
                            ((adapter.data[position]) as ResourceEntity).id
                        )
                    )
                }
            }
            else -> {
                Choose.visibility = View.GONE
                titleTv.text = "select theme"
                val aRes = ResourceManager.get().getAnswerRes(this, R.mipmap::class.java, "mipmap")
                val rRes = ResourceManager.get().getRejectRes(this, R.mipmap::class.java, "mipmap")
                waitingData(aRes, 7)
                waitingData(rRes, 7)
                themeData = ResourceManager.get().mergeRes(aRes, rRes)
                myAdapter2 = MyAdapter2(this, R.layout.layout_item_2, themeData)
                recycler.layoutManager = GridLayoutManager(this, 2)
                recycler.adapter = myAdapter2
                setDefault(MMKV.defaultMMKV()!!.decodeInt("theme", -1), myAdapter2!!)
                myAdapter2!!.setOnItemClickListener { adapter, view, position ->
                    setDefault(position, adapter as MyAdapter2)
                    MMKV.defaultMMKV()!!.encode("theme", position)
                }
            }
        }
        backIcon.setOnClickListener {
            finish()
        }
    }

    private fun openGallery() {
        PictureSelector.create(this).openGallery(PictureMimeType.ofVideo()).previewVideo(true)
            .imageEngine(GlideEngine.createGlideEngine()).forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val list = PictureSelector.obtainMultipleResult(data)
            val path = list[0].path
            startPreViewActivity(path)
        }
    }

    private fun startPreViewActivity(path: String) {
        val i = Intent(this, PreviewActivity::class.java)
        i.putExtra(
            "path",
            path
        )
        startActivity(i)
    }

    private fun waitingData(data: ArrayList<*>, max: Int) {
        while (data.size < max) {
            showDlg()
        }
        closeDlg()
    }

    private fun setDefault(
        path: String,
        adapter: MyAdapter
    ) {
        if (!TextUtils.isEmpty(path)) {
            val data = adapter.data as ArrayList<ResourceEntity>
            val i = data.iterator()
            while (i.hasNext()) {
                val entity = i.next()
                entity.isSelect =
                    TextUtils.equals(path, ResourceManager.get().resId2String(this, entity.id))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun setDefault(
        position: Int,
        adapter: MyAdapter2
    ) {
        if (position != -1) {
            val data = adapter.data as ArrayList<ResourceEntity2>
            for ((index, value) in data.withIndex()) {
                value.isSelect = index == position
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun showDlg() {
        if (dialog == null) {
            dialog = LoadingDialog(this)
        }
        if (!dialog!!.isShowing) {
            dialog!!.show()
        }

    }

    private fun closeDlg() {
        if (dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}