package com.orange.oglite_glitter.Plugins

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jianzhi.glitter.GlitterActivity
import com.jianzhi.glitter.GlitterActivity.Companion.instance
import com.jianzhi.glitter.JavaScriptInterFace

object PerMission {
    fun initial(){
        arrayOf(
            //權限請求
            JavaScriptInterFace("PerMission_Request"){
            request->
            val permission=request.receiveValue["permission"] as ArrayList<String>
            var requestSuccess = 0
            var requestCount=0
            val notPermission:ArrayList<String> = arrayListOf()
            getPermission(permission.toTypedArray(), object : permission_C {
                override fun requestSuccess(a: String) {
                    requestCount += 1
                    requestSuccess += 1
                    if (requestSuccess == permission.size) {
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
        }).map {
            GlitterActivity.addJavacScriptInterFace(it)
        }

    }

    fun getPermission(Permissions: Array<String>, caller: permission_C) {
        val permissionDeniedList = ArrayList<String>()
        for (permission in Permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(instance(), permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                caller.requestSuccess(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(instance(), deniedPermissions, 100)
        }
    }
    interface permission_C {
        fun requestSuccess(a: String)
        fun requestFalse(a: String)
    }
}