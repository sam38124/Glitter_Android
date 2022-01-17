package com.jianzhi.glitter

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jianzhi.glitter.plugins.FileManager
import com.jianzhi.glitter.plugins.GpsManager
import com.jianzhi.glitter.plugins.SoundManager
import com.orange.oglite_glitter.DataBasePlugins
import com.orange.oglite_glitter.Plugins.PerMission

class GlitterInterFace(var webview: WebView) {
    var handler = Handler(Looper.getMainLooper())

    init { create() }

    companion object{
        //調用原生插件
        fun run(
            functionName: String,
            obj: MutableMap<String, Any>,
            finish: (MutableMap<String, Any>) -> Unit
        ) {
            val requestFunction = RequestFunction(obj)
            requestFunction.fin = {
                Handler(Looper.getMainLooper()).post {
                    finish(requestFunction.responseValue)
                }
            }
            if (GlitterActivity.javaScriptInterFace.filter { it.functionName == functionName }.size === 1) {
                GlitterActivity.javaScriptInterFace.filter { it.functionName == functionName }[0].function(requestFunction)
            }else{
                finish(mutableMapOf("data" to "Function not define"))
            }
        }
    }

    /**JS函式路口**/
    @JavascriptInterface
    fun runJsInterFace(data: String) {
        Thread {
            val mapData =
                Gson().fromJson<MutableMap<String, Any>>(data, object : TypeToken<MutableMap<String, Any>>() {}.type)
            val functionName = mapData["functionName"] as String
            val callbackID = (mapData["callBackId"] as Double).toInt()
            val receiveValue: MutableMap<String, Any> =
                if (mapData["data"] == null) mutableMapOf() else (mapData["data"] as MutableMap<String, Any>)
            val cFunction = GlitterActivity.javaScriptInterFace.filter { it.functionName == functionName }
            val requestFunction = RequestFunction(receiveValue)
            requestFunction.fin = {
                handler.post {
                    webview.evaluateJavascript(
                        """
                glitter.callBackList.get(${callbackID})(${Gson().toJson(requestFunction.responseValue)});
                glitter.callBackList.delete(${callbackID});
                """.trimIndent(), null
                    )
                }
            }
            requestFunction.calb = {
                handler.post {
                    webview.evaluateJavascript(
                        """
               glitter.callBackList.get(${callbackID})(${Gson().toJson(requestFunction.responseValue)});
                """.trimIndent(), null
                    )
                }
            }
            if (cFunction.isNotEmpty()) {
                try{
                    cFunction[0].function(requestFunction)
                }catch (e:Exception){
                    requestFunction.responseValue["error"]=e.message.toString()
                    requestFunction.fin()
                    e.printStackTrace()
                }
            } else {
                requestFunction.finish()
            }

        }.start()
    }

    /**加載所有Glitter函式**/
    private fun create() {
        /************************************************************
         * SharedPreferences
         *************************************************************/
        //Get SharedPreferences
        GlitterActivity.addJavacScriptInterFace(GlitterFunction.getPro)
        //Set SharedPreferences
        GlitterActivity.addJavacScriptInterFace(GlitterFunction.setPro)
        //closeApp
        GlitterActivity.addJavacScriptInterFace(GlitterFunction.closeApp)
        //檔案管理
        FileManager.initial()
        //定位功能
        GpsManager.initial()
        //權限請求
        PerMission.initial()
        //資料庫請求
        DataBasePlugins.initial()
        //聲音管理工具
        SoundManager.initial()
    }

}