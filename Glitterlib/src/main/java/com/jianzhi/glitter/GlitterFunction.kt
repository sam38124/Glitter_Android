package com.jianzhi.glitter

import android.content.Context
import com.jzsql.lib.mmySql.JzSqlHelper
import java.io.File

object GlitterFunction {
    val getPro = JavaScriptInterFace("getPro") {
        val act = GlitterActivity.instance()
        val tag = it.receiveValue["name"].toString()
        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val string: String? = profilePreferences.getString(tag, null)
        if (string != null) {
            it.responseValue["data"] = string
        }
        it.finish()
    }
    val setPro = JavaScriptInterFace("setPro") {
        val act = GlitterActivity.instance()
        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putString(it.receiveValue["name"].toString(), it.receiveValue["data"].toString())
            .commit()
        it.responseValue["result"] = true
        it.finish()
    }

    //關閉APP
    val closeApp = JavaScriptInterFace("closeAPP") {
        val act = GlitterActivity.instance()
        act.handler.post {act.finish() }
    }



}