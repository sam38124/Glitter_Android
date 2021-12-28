package com.jianzhi.glitter.plugins

import android.media.MediaPlayer
import android.net.Uri
import com.jianzhi.glitter.GlitterActivity.Companion.instance
import com.jianzhi.glitter.JavaScriptInterFace
import java.io.File

object SoundManager {
    var mediiaplay: MediaPlayer? = null
    fun initial(){
        /**
        * 聲音播放FromAssets
        * */
        JavaScriptInterFace("SoundManager_PlayAssets") { request ->
            try {
                val rout = request.receiveValue["rout"].toString()
                val afd = instance().assets.openFd(rout);
                if (mediiaplay == null) {
                    mediiaplay = MediaPlayer()
                } else {
                    mediiaplay!!.reset()
                }
                mediiaplay!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                mediiaplay!!.prepare()
                mediiaplay!!.start()
                request.responseValue["result"] = true
                request.finish()
            }catch (e:Exception){
                request.responseValue["result"] = false
                request.finish()
            }

        }
        /**
         * 聲音播放FromFile
         * */
        JavaScriptInterFace("SoundManager_PlayFile") { request ->
            try {
                val rout = request.receiveValue["rout"].toString()
                if (mediiaplay == null) {
                    mediiaplay = MediaPlayer()
                } else {
                    mediiaplay!!.reset()
                }

                mediiaplay!!.setDataSource(instance().applicationContext ,  Uri.fromFile(File(instance().applicationContext.filesDir.absolutePath+"/${rout}")))
                mediiaplay!!.prepare()
                mediiaplay!!.start()
                request.responseValue["result"] = true
                request.finish()
            }catch (e:Exception){
                request.responseValue["result"] = false
                request.finish()
            }

        }
    }
}