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
        val buffer = ByteArray(8192)
        val reader = DataInputStream(conn.inputStream)
        val file= File(GlitterActivity.instance().applicationContext.filesDir, fileName)
        if(!file.exists()){
            if(fileName.contains("/")){
                if(!file.parentFile.exists()){
                    file.parentFile.mkdirs();
                }
            }
            file.createNewFile()
        }
        val fileStream=file.outputStream()
        reader.use {
            var read: Int
            while (reader.read(buffer).also { read = it } != -1) {
                fileStream.write(buffer.copyOfRange(0, read))
            }
        }
        fileStream.close()
        reader.close()
        return true
    }catch (e:Exception){
        e.printStackTrace()
        return false
    }
}