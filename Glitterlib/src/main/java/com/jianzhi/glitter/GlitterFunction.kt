package com.jianzhi.glitter

import android.content.Context

object GlitterFunction {
    fun create(){
        //取得紀錄
        GlitterActivity.addJavacScriptInterFace(JavaScriptInterFace("getPro") {
            val act = GlitterActivity.instance()
            val tag = it.receiveValue["name"].toString()
            val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
            val string: String? = profilePreferences.getString(tag, null)
            if(string!=null){ it.responseValue["data"]=string }
            it.finish()
        })
        //存紀錄
        GlitterActivity.addJavacScriptInterFace(JavaScriptInterFace("setPro"){
            val act = GlitterActivity.instance()
            val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
            profilePreferences.edit().putString(it.receiveValue["name"].toString(), it.receiveValue["data"].toString()).commit()
            it.responseValue["result"]=true
            it.finish()
        })
        //s
    }
}