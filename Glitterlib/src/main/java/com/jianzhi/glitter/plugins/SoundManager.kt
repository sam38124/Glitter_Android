package com.jianzhi.glitter.plugins

import android.media.MediaPlayer
import com.jianzhi.glitter.GlitterActivity.Companion.instance
import com.jianzhi.glitter.JavaScriptInterFace

object SoundManager {
    fun initial(){
        var canPlayMedia=false
        arrayOf(
            /**
            * 聲音播放 [rout:String]
            * */
            JavaScriptInterFace("SoundManager_Play"){
                    request->
                if (!canPlayMedia) {
                    request.responseValue["result"]=false
                    request.finish()
                    return@JavaScriptInterFace
                }
                val rout=request.receiveValue["rout"].toString()
                canPlayMedia = false
                val assetRout = rout
                val afd = instance().assets.openFd(assetRout);
                val mediiaplay = MediaPlayer()
                mediiaplay.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                mediiaplay.prepare()
                mediiaplay.setOnCompletionListener {
                    mediiaplay.release()
                    canPlayMedia = true
                }
                mediiaplay.start()
                request.responseValue["result"]=true
                request.finish()
            }
        ).map{}
    }
}