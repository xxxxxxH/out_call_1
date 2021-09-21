package net.basicmodel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.luck.picture.lib.tools.ToastUtils
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_activity_block.*
import kotlinx.android.synthetic.main.layout_title_bar.*
import net.adapter.ContactAdapter
import net.entity.ContactsEntity
import net.utils.ContactsManager
import net.utils.LoadingDialog
import net.utils.MMKVUtils

class BlockActivity : AppCompatActivity() {

    var data: ArrayList<ContactsEntity>? = null
    var dialog: LoadingDialog? = null
    var contactAdapter: ContactAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_block)
        initData()
        initEditText()
    }

    private fun initData() {
        data = ContactsManager.get().getAllContacts(this)
        setIsLock(data!!)
        contactAdapter = ContactAdapter(R.layout.layout_item_contact, data)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = contactAdapter
        contactAdapter!!.setOnItemClickListener { adapter, view, position ->
            ToastUtils.s(this@BlockActivity, "2")
        }
        contactAdapter!!.addChildClickViewIds(R.id.blockThis)
        contactAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.blockThis) {
                val entity = adapter.data[position] as ContactsEntity
                entity.isLock = !entity.isLock
                MMKVUtils.saveKeys("id", entity.id)
                MMKV.defaultMMKV()!!.encode(entity.id.toString(), entity.isLock)
            }
            adapter.notifyDataSetChanged()
        }
        backIcon.setOnClickListener {
            finish()
        }
    }

    private fun initEditText() {
        searchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val index = findItem(p0.toString())
                if (index != -1) {
                    recycler.smoothScrollToPosition(index)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun findItem(key: String): Int {
        var index = -1
        for ((i, item) in data!!.withIndex()) {
            if (item.name.toLowerCase().contains(key.toLowerCase())) {
                index = i
                break
            }
        }
        return index
    }

    private fun setIsLock(data: ArrayList<ContactsEntity>) {
        val keys = MMKV.defaultMMKV()!!.decodeStringSet("id") as HashSet<String>?
        if (keys != null) {
            for (item in data) {
                for (item1 in keys) {
                    if (item1.contains(item.id)) {
                        val status = MMKV.defaultMMKV()!!.decodeBool(item.id.toString())
                        item.isLock = status
                    }
                }
            }
        }
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