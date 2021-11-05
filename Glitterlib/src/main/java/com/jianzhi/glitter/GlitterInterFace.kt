package com.jianzhi.glitter

import android.os.Handler
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jianzhi.glitter.plugins.FileManager
import com.jianzhi.glitter.plugins.GpsManager
import com.jianzhi.glitter.plugins.SoundManager
import com.orange.oglite_glitter.DataBasePlugins
import com.orange.oglite_glitter.Plugins.PerMission

class GlitterInterFace {
    var handler = Handler()

    init {
        create()
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
                    GlitterActivity.instance().webRoot.evaluateJavascript(
                        """
                glitter.callBackList.get(${callbackID})(${Gson().toJson(requestFunction.responseValue)});
                glitter.callBackList.delete(${callbackID});
                """.trimIndent(), null
                    )
                }
            }
            requestFunction.calb = {
                handler.post {
                    GlitterActivity.instance().webRoot.evaluateJavascript(
                        """
               glitter.callBackList.get(${callbackID})(${Gson().toJson(requestFunction.responseValue)});
                """.trimIndent(), null
                    )
                }
            }
            if (cFunction.isNotEmpty()) {
                cFunction[0].function(requestFunction)
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