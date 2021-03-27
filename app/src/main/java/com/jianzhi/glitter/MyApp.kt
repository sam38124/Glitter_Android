package com.jianzhi.glitter

import android.app.Application
import com.jianzhi.glitter.GlitterActivity
import com.jianzhi.glitter.JsInterFace

class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()
        GlitterActivity.setUp("file:///android_asset/petagram/appData",appName = "appData")

    }
}

