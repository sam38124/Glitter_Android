package com.orange.oglite_glitter

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.jianzhi.glitter.GlitterActivity
import com.jianzhi.glitter.GlitterActivity.Companion.instance
import com.jianzhi.glitter.JavaScriptInterFace
import com.jzsql.lib.mmySql.JzSqlHelper
import java.io.File
import java.lang.Exception

/**
 * DataBase開發套件
 * */
object DataBasePlugins {
    fun initial() {
        val handler= Handler(Looper.getMainLooper())
        val dataMap: MutableMap<String, JzSqlHelper> = mutableMapOf()
        /**
         * 從本地端加載資料庫
         * request->[rout:String,name:String]
         * response->[result:Boolean]
         * */
        JavaScriptInterFace("DataBase_InitByLocal") { request ->
            try {
                val rout = request.receiveValue["rout"].toString()
                val name = request.receiveValue["name"].toString()
                if (dataMap[name] == null) {
                    handler.post {
                        dataMap[name] = JzSqlHelper(GlitterActivity.glitterApplication, name)
                    }
                    Thread.sleep(1000)
                }
                dataMap[name]!!.close()

                val file = File(GlitterActivity.glitterApplication.filesDir, rout)
                if (!file.exists()) {
                    if (rout.contains("/")) {
                        if (!file.parentFile.exists()) {
                            file.parentFile.mkdirs();
                        }
                    }
                    file.createNewFile()
                }
                dataMap[name]!!.dbinit(file.inputStream())
                dataMap[name]!!.create()
                request.responseValue["result"] = true
            } catch (e: Exception) {
                request.responseValue["result"] = false
            }
            request.finish()
        }
        /**
         * 藉由Assets路徑加載資料庫
         * request->[rout:String,name:String]
         * response->[result:Boolean]
         * */
        JavaScriptInterFace("DataBase_InitByAssets") { request ->
            try {
                val name = request.receiveValue["name"].toString()
                val rout = request.receiveValue["rout"].toString()
                val act = GlitterActivity.glitterApplication
                if (dataMap[name] == null) {
                    handler.post {
                        dataMap[name] = JzSqlHelper(GlitterActivity.glitterApplication, name)
                    }
                    Thread.sleep(1000)
                }
                dataMap[name]!!.close()
                if (GlitterActivity.baseRout.contains("file:///android_asset")) {
                    val assetRout = "${
                        GlitterActivity.baseRout.replace(
                            "file:///android_asset/",
                            ""
                        )
                    }/${rout.replace("file:/android_asset/", "")}"
                    dataMap[name]!!.dbinit(act.assets.open(assetRout))
                    dataMap[name]!!.create()
                } else {
                    val file = File("${GlitterActivity.baseRout}/$rout")
                    dataMap[name]!!.dbinit(file.inputStream())
                    dataMap[name]!!.create()
                }
                request.responseValue["result"] = true
            } catch (e: Exception) {
                request.responseValue["result"] = false
            }
            request.finish()
        }
        /**
         * Sql查詢
         * request->[string:String,name:String]
         * response->[result:Boolean,data:Map]
         * */
        JavaScriptInterFace("DataBase_Query") { request ->
            try {
                val name = request.receiveValue["name"].toString()
                val string = request.receiveValue["string"].toString()
                if (dataMap[name] == null) {
                    handler.post {
                        dataMap[name] = JzSqlHelper(GlitterActivity.glitterApplication, name)
                    }
                    Thread.sleep(1000)
                }
                val mapArray: ArrayList<MutableMap<String, Any>> = arrayListOf()
                dataMap[name]!!.query(string) {
                    val map: MutableMap<String, Any> = mutableMapOf()
                    for (a in 0 until it.columnCount) {
                        if (it.getString(a) != null) {
                            map[it.getColumnName(a)] = it.getString(a)
                        }
                    }
                    mapArray.add(map)
                }
                request.responseValue["data"] = mapArray
                request.responseValue["result"] = true
            } catch (e: Exception) {
                request.responseValue["result"] = false
            }
            request.finish()
        }
        /**
         * Sql操作
         * request->[string:String,name:String]
         * response->[result:Boolean]
         * */
        JavaScriptInterFace("DataBase_exSql") { request ->
            val name = request.receiveValue["name"].toString()
            val string = request.receiveValue["string"].toString()
            if (dataMap[name] == null) {
                handler.post {
                    dataMap[name] = JzSqlHelper(GlitterActivity.glitterApplication, name)
                }
                Thread.sleep(1000)
            }
            dataMap[name]!!.exsql(string)
            request.responseValue["result"] = true
            request.finish()
        }
    }
}