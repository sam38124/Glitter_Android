package com.jianzhi.glitter

import android.content.Context
import android.os.Build
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

    //取得系統版本資訊
    val getSystemVersion=JavaScriptInterFace("getSystemVersion") {
        request->
        /**
         * 獲取當前手機系統版本號
         *
         * @return  系統版本號
         */
        fun getSystemVersion(): String {
            return Build.VERSION.RELEASE
        }

        /**
         * 獲取手機型號
         *
         * @return  手機型號
         */
        fun getSystemModel(): String {
            return Build.MODEL
        }
        /**
         * 獲取手機品牌
         *
         * @return  手機型號
         */
        fun getSystemMake(): String {
            return Build.BRAND;
        }
        request.responseValue["version"]=getSystemVersion()
        request.responseValue["model"]=getSystemModel()
        request.responseValue["make"]=getSystemMake()
        request.finish()
    }

}