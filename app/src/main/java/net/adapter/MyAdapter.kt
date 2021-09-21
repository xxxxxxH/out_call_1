package net.adapter

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.ResourceEntity
import net.entity.ScreenUtils
import net.utils.ResourceManager

class MyAdapter(
    private val activity: Activity,
    layoutResId: Int,
    data: ArrayList<ResourceEntity>?
) :
    BaseQuickAdapter<ResourceEntity, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: ResourceEntity) {

        val imageView = holder.getView<ImageView>(R.id.itemImg)
        val select = holder.getView<ImageView>(R.id.itemSelect)
        val itemRoot = holder.getView<RelativeLayout>(R.id.itemRoot)
        val p = itemRoot.layoutParams
        p.width = ScreenUtils.getScreenSize(activity)[1] / 2
        p.height = ScreenUtils.getScreenSize(activity)[0] / 2
        itemRoot.layoutParams = p
        Glide.with(activity).load(ResourceManager.get().resId2String(activity, item.id))
            .placeholder(R.color.black)
            .into(imageView)
        select.visibility = if (item.isSelect) View.VISIBLE else View.GONE
    }
}