package com.jianzhi.glitter

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.jzsql.lib.mmySql.JzSqlHelper
import java.io.File

object GlitterFunction {
    val getPro = JavaScriptInterFace("getPro") {
        val tag = it.receiveValue["name"].toString()
        val profilePreferences = GlitterActivity.glitterApplication.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val string: String? = profilePreferences.getString(tag, null)
        if (string != null) {
            it.responseValue["data"] = string
        }
        it.finish()
    }
    val setPro = JavaScriptInterFace("setPro") {
        val profilePreferences = GlitterActivity.glitterApplication.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putString(it.receiveValue["name"].toString(), it.receiveValue["data"].toString())
            .commit()
        it.responseValue["result"] = true
        it.finish()
    }

    //關閉APP
    val closeApp = JavaScriptInterFace("closeAPP") {
        val act = GlitterActivity.instance()
        Handler(Looper.getMainLooper()).post {act.finish()  }
    }



}