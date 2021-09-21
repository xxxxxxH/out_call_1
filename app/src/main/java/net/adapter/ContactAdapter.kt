package net.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.ContactsEntity

class ContactAdapter(layoutResId: Int, data: ArrayList<ContactsEntity>?) :
    BaseQuickAdapter<ContactsEntity, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: ContactsEntity) {
        holder.setText(R.id.contactName, item.name)
            .setText(R.id.contactNumber, item.phone)
            .setBackgroundResource(
                R.id.blockThis,
                if (item.isLock) R.mipmap.unblock else R.mipmap.blocke
            )

        Glide.with(context).load(item.avt).placeholder(R.mipmap.def)
            .into(holder.getView(R.id.contactImage))
    }
}