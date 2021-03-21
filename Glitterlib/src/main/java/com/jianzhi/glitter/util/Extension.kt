package com.jianzhi.glitter.util

import android.util.Log
import com.jianzhi.glitter.GlitterActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL


fun String.downloadFile(timeOut:Int,fileName:String):Boolean{
    try {
        val conn: HttpURLConnection = URL(this).openConnection() as HttpURLConnection
        conn.connectTimeout = timeOut
        conn.readTimeout = timeOut
        conn.requestMethod = "GET"
        conn.doInput = true;
        val buffer = ByteArray(1024)
        val reader = DataInputStream(conn.inputStream)
        val strBuf=ByteArrayOutputStream()
        var downLoad = 0L
        val file= File(GlitterActivity.instance().applicationContext.filesDir, fileName)
        if(!file.exists()){
            file.createNewFile()
        }
        reader.use {
            var read: Int
            while (reader.read(buffer).also { read = it } != -1) {
                downLoad += read
                strBuf.write(buffer.copyOfRange(0, read))
            }
        }
        file.writeBytes(strBuf.toByteArray())
        reader.close()
        return true
    }catch (e:Exception){
        e.printStackTrace()
        return false
    }
}