package com.jianzhi.glitter

import android.app.Application
import android.content.Intent
import android.util.Log
import com.jianzhi.glitter.GlitterActivity.ResultCallBack

class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()
        GlitterInterFace.run("FileManager_CheckFileExists", mutableMapOf("route:text" to "data.text")) {
            Log.d("filexists",it["result"].toString())
        }
        GlitterActivity.setUp("file:///android_asset/sample",appName = "sample")
        GlitterActivity.addActivityResult(object : ResultCallBack {
        override fun resultBack(requestCode: Int, resultCode: Int, data: Intent) {} })
    }

}

