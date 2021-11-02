//package com.jianzhi.glitter.service
//
//import com.jianzhi.glitter.GlitterActivity
//import com.jianzhi.glitter.JavaScriptInterFace
//import com.jzsql.lib.mmySql.JzSqlHelper
//import java.io.File
//
///**DataBase開發套件**/
//object Service_DataBase {
//    //資料庫列表
//    val dataMap: MutableMap<String, JzSqlHelper> = mutableMapOf()
//
//    fun create() {
//        arrayOf(
//            /**
//             * 執行資料庫操作
//             * request:[name:DataBase名稱 , sql:sql字串 , rout?:資料庫路徑]
//             * response:[result:執行結果]
//             * */
//            JavaScriptInterFace("DataBase_exSql") {
//                val name = it.receiveValue["name"].toString()
//                val sql = it.receiveValue["sql"] as ArrayList<*>
//                val rout = it.receiveValue["rout"]
//                val isAsset = it.receiveValue["isAsset"]
//                if (dataMap[name] == null) {
//                    createDataBase(rout, isAsset, name)
//                }
//                sql.map {
//                    dataMap[name]!!.exsql(it as String)
//                }
//                it.responseValue["result"]=true
//                it.finish()
//            },
//            /**
//             * 查詢資料庫
//             * request:[name:DataBase名稱 , isAsset:是否存於Glitter資料夾中 , rout?:資料庫路徑 , sql:sql字串]
//             * response:[result:執行結果,data:陣列物件]
//             * */
//            JavaScriptInterFace("DataBase_query") {
//                val name = it.receiveValue["name"].toString()
//                val string = it.receiveValue["sql"].toString()
//                val rout = it.receiveValue["rout"]
//                val isAsset = it.receiveValue["isAsset"]
//                if (dataMap[name] == null) {
//                    createDataBase(rout, isAsset, name)
//                }
//                val mapArray: ArrayList<MutableMap<String, Any>> = arrayListOf()
//                dataMap[name]!!.query(string) {
//                    val map: MutableMap<String, Any> = mutableMapOf()
//                    for (a in 0 until it.columnCount) {
//                        if (it.getString(a) != null) {
//                            map[it.getColumnName(a)] = it.getString(a)
//                        }
//                    }
//                    mapArray.add(map)
//                }
//                it.responseValue["result"] = true
//                it.responseValue["data"] = mapArray
//                it.finish()
//            }
//        ).map {
//            GlitterActivity.addJavacScriptInterFace(it)
//        }
//    }
//    //創建資料庫
//    private fun createDataBase(rout: Any?, isAsset: Any?, name: String) {
//        val act=GlitterActivity.instance().applicationContext
//        dataMap[name] = JzSqlHelper(act, name)
//        if (rout != null) {
//            dataMap[name]!!.close()
//            if (isAsset==true) {
//                dataMap[name]!!.dbinit(act.assets.open(rout.toString()))
//                dataMap[name]!!.create()
//            } else {
//                val file = File(rout.toString())
//                dataMap[name]!!.dbinit(file.inputStream())
//                dataMap[name]!!.create()
//            }
//        }
//    }
//}