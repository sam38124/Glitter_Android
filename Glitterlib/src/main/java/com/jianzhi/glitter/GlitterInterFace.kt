package com.jianzhi.glitter

import android.os.Handler
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jianzhi.glitter.service.Service_DataBase

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
        //Database操作
        Service_DataBase.create()
    }

}