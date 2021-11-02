//package com.jianzhi.glitter.plugins
//
//import android.content.Context
//import android.util.Log
//import android.webkit.JavascriptInterface
//import com.google.gson.Gson
//import com.jianzhi.glitter.GlitterActivity
//import com.jianzhi.glitter.JavaScriptInterFace
//import com.jzsql.lib.mmySql.JzSqlHelper
//import java.io.File
//
///**
// * DataBase開發套件
// * */
//class DataBase {
//    ///資料庫集合
//    var dataMap: MutableMap<String, JzSqlHelper> = mutableMapOf()
//    ///藉由本地路徑加載資料庫
//    val initByLocal = JavaScriptInterFace("DataBase_InitByLocal") { request ->
//        val act = GlitterActivity.instance()
//        val rout = request.receiveValue["rout"].toString()
//        val name = request.receiveValue["name"].toString()
//        if (dataMap[name] == null) {
//            dataMap[name] = JzSqlHelper(act, name)
//        }
//        dataMap[name]!!.close()
//        val file = File(act.filesDir, rout)
//        if (!file.exists()) {
//            if (rout.contains("/")) {
//                if (!file.parentFile.exists()) {
//                    file.parentFile.mkdirs();
//                }
//            }
//            file.createNewFile()
//        }
//        dataMap[name]!!.dbinit(file.inputStream())
//        dataMap[name]!!.create()
//    }
//    ///藉由Assets路徑加載資料庫
//    val initByAssets = JavaScriptInterFace("DataBase_InitByAssets") { request ->
//        val name = request.receiveValue["name"].toString()
//        val rout = request.receiveValue["rout"].toString()
//        val act = GlitterActivity.instance()
//        if (dataMap[name] == null) {
//            dataMap[name] = JzSqlHelper(act, name)
//        }
//        dataMap[name]!!.close()
//        if (GlitterActivity.baseRout.contains("file:///android_asset")) {
//            val assetRout =
//                "${
//                    GlitterActivity.baseRout.replace(
//                        "file:///android_asset/",
//                        ""
//                    )
//                }/${rout.replace("file:/android_asset/", "")}"
//            dataMap[name]!!.dbinit(act.assets.open(assetRout))
//            dataMap[name]!!.create()
//        } else {
//            val file = File("${GlitterActivity.baseRout}/$rout")
//            dataMap[name]!!.dbinit(file.inputStream())
//            dataMap[name]!!.create()
//        }
//    }
//    ///查詢
//    val query = JavaScriptInterFace("DataBase_Query"){
//        request ->
//        val name=request.receiveValue["name"].toString()
//        val string=request.receiveValue["string"].toString()
//        if (dataMap[name] == null) {
//            dataMap[name] = JzSqlHelper(GlitterActivity.instance(), name)
//        }
//
//        val mapArray: ArrayList<MutableMap<String, Any>> = arrayListOf()
//        dataMap[name]!!.query(string) {
//            val map: MutableMap<String, Any> = mutableMapOf()
//            for (a in 0 until it.columnCount) {
//                if (it.getString(a) != null) {
//                    map[it.getColumnName(a)] = it.getString(a)
//                }
//            }
//            mapArray.add(map)
//        }
//        Log.e("DataBase", Gson().toJson(mapArray))
//        return Gson().toJson(mapArray)
//    }
//    @JavascriptInterface
//    fun exSql(name: String, string: String) {
//        if (dataMap[name] == null) {
//            dataMap[name] = JzSqlHelper(this@GlitterActivity, name)
//        }
//        dataMap[name]!!.exsql(string)
//    }
//
//
//}
//}