package com.jianzhi.glitter

import android.app.Application

class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()

        GlitterActivity.setUp("file:///android_asset/Petagram",appName = "Petagram")
    }

}

