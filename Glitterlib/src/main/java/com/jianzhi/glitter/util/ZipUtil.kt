package com.jianzhi.glitter.util

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

object ZipUtil {
    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: String) {
        Log.e("zipFilePath","$zipFilePath")
        val destDir = File(destDirectory)
        if (!destDir.exists()) {
            destDir.mkdir()
        }
        val zipIn = ZipInputStream(FileInputStream(zipFilePath))
        var entry = zipIn.nextEntry
        // iterates over entries in the zip file
        var first=true
        while (entry != null) {
            val filePath = destDirectory + File.separator.toString() + entry.name
            if (!entry.isDirectory) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath)
            } else {
                // if the entry is a directory, make the directory
                val dir = File(filePath)
                dir.mkdirs()
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
        zipIn.close()
    }

    @Throws(IOException::class)
    private fun extractFile(zipIn: ZipInputStream, filePath: String) {
        File(filePath).parentFile.mkdirs()
        val bos = BufferedOutputStream(FileOutputStream(filePath))
        val bytesIn = ByteArray(4096)
        var read = 0
        while (zipIn.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }
    fun getRequest(
        url: String, timeout: Int, downloadProgress: (a: Int) -> Unit = {}
        , file: File
    ): Boolean {
        try {
            val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = timeout
            conn.readTimeout = timeout
            conn.requestMethod = "GET"
            conn.doInput = true;
            val buffer = ByteArray(1024)
            val reader = DataInputStream(conn.inputStream)
            var downLoad = 0L
            var fileOutPutStream= FileOutputStream(file)
            reader.use {
                var read: Int
                while (reader.read(buffer).also { read = it } != -1) {
                    downLoad += read
                    fileOutPutStream.write(buffer.copyOfRange(0, read))
                    if (reader.available() > 0) {
                        downloadProgress((downLoad * 100 / reader.available()).toInt())
                    }
                }
            }
            reader.close()
            fileOutPutStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}