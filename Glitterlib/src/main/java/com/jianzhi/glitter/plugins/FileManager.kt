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
            }
            /**
             * 取得檔案
             * request->[fileName:String,type:String]
             * response->[result:Boolean]
             * */
            ,
            JavaScriptInterFace("FileManager_GetFile"){
                request->
                try {
                    val type=request.receiveValue["type"].toString()
                    val fileName=request.receiveValue["fileName"].toString()
                    when(type){
                        "hex" -> {
                            request.responseValue["data"]= File(
                                GlitterActivity.instance().applicationContext.filesDir,
                                fileName
                            ).readBytes().toHex()
                        }
                        "bytes" -> {
                            request.responseValue["data"]= File(
                                GlitterActivity.instance().applicationContext.filesDir,
                                fileName
                            ).readBytes()
                        }
                        "text" -> {
                            request.responseValue["data"]= File(
                                GlitterActivity.instance().applicationContext.filesDir,
                                fileName
                            ).readText()
                        }
                    }
                    request.responseValue["result"]=true
                    request.finish()
                }catch (e:Exception){
                    e.printStackTrace()
                    request.responseValue["result"]=false
                }
            }
        ).map {
            GlitterActivity.addJavacScriptInterFace(it)
        }
    }
}