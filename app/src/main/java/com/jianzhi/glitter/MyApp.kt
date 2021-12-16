package com.jianzhi.glitter

import android.app.Application
import android.content.Intent
import com.jianzhi.glitter.GlitterActivity.ResultCallBack

class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()

        GlitterActivity.setUp("file:///android_asset/sample",appName = "sample")
        GlitterActivity.addActivityResult(object : ResultCallBack {
        override fun resultBack(requestCode: Int, resultCode: Int, data: Intent) {} })
    }

}

