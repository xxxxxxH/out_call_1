package net.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import net.entity.ResourceEntity
import net.entity.ResourceEntity2
import java.util.*

/**
 * Copyright (C) 2021,2021/9/14, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class ResourceManager {
    companion object {
        private var instance: ResourceManager? = null
            get() {
                field?.let {

                } ?: run {
                    field = ResourceManager()
                }
                return field
            }

        @Synchronized
        fun get(): ResourceManager {
            return instance!!
        }
    }

    fun getAllResource(context: Context, clazz: Class<*>, type: String): ArrayList<ResourceEntity> {
        val result = ArrayList<ResourceEntity>()
        Thread {
            for (field in clazz.fields) {
                val name = field.name
                if (name.startsWith("pic"))
                    continue
                val id = context.resources.getIdentifier(name, type, context.packageName)
                val entity = ResourceEntity()
                entity.name = name
                entity.id = id
                result.add(entity)
            }
        }.start()
        return result
    }

    fun getAnswerRes(context: Context, clazz: Class<*>, type: String): ArrayList<ResourceEntity> {
        val result = ArrayList<ResourceEntity>()
        Thread {
            for (field in clazz.fields) {
                val name = field.name
                if (name.contains("answer")) {
                    val id = context.resources.getIdentifier(name, type, context.packageName)
                    val entity = ResourceEntity()
                    entity.name = name
                    entity.id = id
                    result.add(entity)
                }
            }
        }.start()
        return result
    }

    fun getRejectRes(context: Context, clazz: Class<*>, type: String): ArrayList<ResourceEntity> {
        val result = ArrayList<ResourceEntity>()
        Thread {
            for (field in clazz.fields) {
                val name = field.name
                if (name.contains("reject")) {
                    val id = context.resources.getIdentifier(name, type, context.packageName)
                    val entity = ResourceEntity()
                    entity.name = name
                    entity.id = id
                    result.add(entity)
                }
            }
        }.start()
        return result
    }

    fun mergeRes(aRes:ArrayList<ResourceEntity>,rRes:ArrayList<ResourceEntity>):ArrayList<ResourceEntity2>{
        val result = ArrayList<ResourceEntity2>()
        for (item1 in aRes){
            for (item2 in rRes){
                if (TextUtils.equals(item1.name.toCharArray()[0].toString(),item2.name.toCharArray()[0].toString())){
                    val entity = ResourceEntity2()
                    entity.name = item1.name
                    entity.id = item1.id
                    entity.name2 = item2.name
                    entity.id2 = item2.id
                    result.add(entity)
                }
            }
        }
        return result
    }


    fun resId2String(context: Context, id: Int): String {
        val r = context.resources
        val uri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(id) + "/"
                    + r.getResourceTypeName(id) + "/"
                    + r.getResourceEntryName(id)
        )
        return uri.toString()
    }
}