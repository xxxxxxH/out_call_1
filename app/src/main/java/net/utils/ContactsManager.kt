package net.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import net.entity.ContactsEntity

class ContactsManager {
    companion object {
        private var instance: ContactsManager? = null
            get() {
                field?.let {

                } ?: run {
                    field = ContactsManager()
                }
                return field
            }

        @Synchronized
        fun get(): ContactsManager {
            return instance!!
        }
    }

    @SuppressLint("Recycle")
    fun getAllContacts(context: Context):ArrayList<ContactsEntity>{
        val result = ArrayList<ContactsEntity>()
        val c = context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        while (c!!.moveToNext()){
            val entity = ContactsEntity()
            val id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
            val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val av = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
            entity.id = id
            entity.name = name
            entity.avt = av
            val c1 = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null)
            while (c1!!.moveToNext()){
                val phone = c1.getString(c1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                entity.phone = phone
            }
            result.add(entity)
            c1.close()
        }
        c.close()
        return result
    }
}