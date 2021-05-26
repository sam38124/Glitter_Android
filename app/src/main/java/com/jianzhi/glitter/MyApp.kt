package com.jianzhi.glitter

import android.app.Application
import android.content.Intent
import com.jianzhi.glitter.GlitterActivity.ResultCallBack

class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()

        GlitterActivity.setUp("file:///android_asset/TireStorage", appName = "TireStorage")
    }

}

