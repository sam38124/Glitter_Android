package com.jianzhi.glitter.plugins

import com.google.gson.Gson
import com.jianzhi.glitter.GlitterActivity
import com.jianzhi.glitter.JavaScriptInterFace
import com.orango.electronic.jzutil.toHex
import java.io.File


/**
 * 檔案下載插件
 * */
object FileManager {
    fun initial() {
        /**
         * 判斷檔案是否存在
         * request->[route:String]
         * response->[result:Boolean]
         * */
        JavaScriptInterFace("FileManager_CheckFileExists") { request ->
            request.responseValue["result"] =
                File(
                    GlitterActivity.glitterApplication.filesDir,
                    request.receiveValue["route"].toString()
                ).exists()
            request.finish()
        }
        /**
         * 取得檔案
         * request->[route:String,type:String]
         * response->[result:Boolean]
         * */
        JavaScriptInterFace("FileManager_GetFile"){
                request->
            try {
                val type=request.receiveValue["type"].toString()
                val route=request.receiveValue["route"].toString()
                when(type){
                    "hex" -> {
                        request.responseValue["data"]= File(
                            GlitterActivity.glitterApplication.filesDir,
                            route
                        ).readBytes().toHex()
                    }
                    "bytes" -> {
                        request.responseValue["data"]= File(
                            GlitterActivity.glitterApplication.filesDir,
                            route
                        ).readBytes()
                    }
                    "text" -> {
                        request.responseValue["data"]= File(
                            GlitterActivity.glitterApplication.filesDir,
                            route
                        ).readText()
                    }
                }
                request.responseValue["result"]=true
            }catch (e:Exception){
                e.printStackTrace()
                request.responseValue["result"]=false
            }
            request.finish()
        }
    }
}