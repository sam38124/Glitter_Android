package com.orange.oglite_glitter.Plugins

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jianzhi.glitter.GlitterActivity
import com.jianzhi.glitter.GlitterActivity.Companion.instance
import com.jianzhi.glitter.JavaScriptInterFace

object PerMission {
    fun initial(){
        /**
         * 取得權限
         * request->[permission:ArrayList<String>]
         * response->[notPermission:ArrayList<String>?,result:Boolean]
         * ---------------------------------
         * */
        JavaScriptInterFace("PerMission_Request"){
                request->
            val permission=request.receiveValue["permission"] as ArrayList<String>
            var requestSuccess = 0
            var requestCount=0
            val notPermission:ArrayList<String> = arrayListOf()
            instance().getPermission(permission.toTypedArray(), object : GlitterActivity.permission_C {
                override fun requestSuccess(a: String) {
                    requestCount += 1
                    requestSuccess += 1
                    if (requestCount == permission.size) {
                        request.responseValue["result"]=true
                        request.finish()
                    }
                }

                override fun requestFalse(a: String) {
                    requestCount += 1
                    notPermission.add(a)
                    if(requestCount==permission.size){
                        request.responseValue["notPermission"]=notPermission
                        request.responseValue["result"]=false
                        request.finish()
                    }
                }
            })
        }

    }

}