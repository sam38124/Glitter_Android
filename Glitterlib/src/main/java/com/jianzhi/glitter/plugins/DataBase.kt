package com.orange.oglite_glitter

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
        ///資料庫集合
        val dataMap: MutableMap<String, JzSqlHelper> = mutableMapOf()
        arrayOf(
            ///藉由本地路徑加載資料庫
            JavaScriptInterFace("DataBase_InitByLocal") { request ->
                try {
                    val act = instance()
                    val rout = request.receiveValue["rout"].toString()
                    val name = request.receiveValue["name"].toString()
                    if (dataMap[name] == null) {
                        instance().handler.post {
                            dataMap[name] = JzSqlHelper(instance(), name)
                        }
                        Thread.sleep(1000)
                    }
                    dataMap[name]!!.close()
                    val file = File(act.filesDir, rout)
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
            },
            ///藉由Assets路徑加載資料庫
            JavaScriptInterFace("DataBase_InitByAssets") { request ->
                try {
                    val name = request.receiveValue["name"].toString()
                    val rout = request.receiveValue["rout"].toString()
                    val act = instance()
                    if (dataMap[name] == null) {
                        instance().handler.post {
                            dataMap[name] = JzSqlHelper(instance(), name)
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
            },
            ///查詢
            JavaScriptInterFace("DataBase_Query") { request ->
                try {
                    val name = request.receiveValue["name"].toString()
                    val string = request.receiveValue["string"].toString()
                    if (dataMap[name] == null) {
                        instance().handler.post {
                            dataMap[name] = JzSqlHelper(instance(), name)
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
            },
            ///執行語法
            JavaScriptInterFace("DataBase_exSql") { request ->
                val name = request.receiveValue["name"].toString()
                val string = request.receiveValue["string"].toString()
                if (dataMap[name] == null) {
                    instance().handler.post {
                        dataMap[name] = JzSqlHelper(instance(), name)
                    }
                    Thread.sleep(1000)
                }
                dataMap[name]!!.exsql(string)
                request.responseValue["result"] = true
                request.finish()
            },
            ///判斷檔案是否存在
            JavaScriptInterFace("checkFileExists") { request ->
                request.responseValue["result"] =
                File(instance().applicationContext.filesDir, request.receiveValue["fileName"].toString()).exists()
                request.finish()
            }

        ).map {
            GlitterActivity.addJavacScriptInterFace(it)
        }
    }
}