package net.adapter

import android.app.Activity
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.ResourceEntity2
import net.entity.ScreenUtils
import net.utils.ResourceManager

class MyAdapter2(
    private val activity: Activity,
    layoutResId: Int,
    data: ArrayList<ResourceEntity2>?,
) :
    BaseQuickAdapter<ResourceEntity2, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: ResourceEntity2) {
        val root = holder.getView<RelativeLayout>(R.id.screenLayout)
        val p = root.layoutParams
        p.height = ScreenUtils.getScreenSize(activity)[0] / 2
        root.layoutParams = p
        val aUrl = ResourceManager.get().resId2String(context, item.id)
        Glide.with(context).load(aUrl).into(holder.getView(R.id.answerButton))
        val rUrl = ResourceManager.get().resId2String(context, item.id2)
        Glide.with(context).load(rUrl).into(holder.getView(R.id.rejectButton))
        holder.setGone(R.id.sel_lay, !item.isSelect)
    }
}