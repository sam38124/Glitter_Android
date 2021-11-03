package com.jianzhi.glitter.plugins

import com.jianzhi.glitter.GlitterActivity
import com.jianzhi.glitter.JavaScriptInterFace
import java.io.File


/**
 * 檔案下載插件
 * */
object FileManager {
    fun initial() {
        arrayOf(
            /**
             * 判斷檔案是否存在
             * request->[fileName:String]
             * response->[result:Boolean]
             * */
            JavaScriptInterFace("FileManager_CheckFileExists") { request ->
                request.responseValue["result"] =
                    File(
                        GlitterActivity.instance().applicationContext.filesDir,
                        request.receiveValue["fileName"].toString()
                    ).exists()
                request.finish()
            }).map {
            GlitterActivity.addJavacScriptInterFace(it)
        }
    }
}