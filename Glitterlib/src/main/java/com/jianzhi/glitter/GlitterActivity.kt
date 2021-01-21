package com.jianzhi.glitter

import android.bluetooth.BluetoothDevice
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.ViewParent
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.jianzhi.jzblehelper.BleHelper
import com.jianzhi.jzblehelper.callback.BleCallBack
import com.jianzhi.jzblehelper.models.BleBinary
import com.jzsql.lib.mmySql.JzSqlHelper
import com.orange.glitter.R
import com.orango.electronic.jzutil.CalculateTime


class GlitterActivity: AppCompatActivity() {
    companion object {
        var baseRout: String = ""
        var updateRout: String? = null
        var appName: String = ""
        var onCreate: (GlitterPage) -> Unit = {}
        lateinit var instance: () -> GlitterActivity
        fun setUp(
            baseRout: String,
            updateRout: String?=null,
            appName: String,
            onCreate: (GlitterPage) -> Unit = {}
        ) {
            this.appName = appName
            this.baseRout = baseRout
            this.updateRout = updateRout
            this.onCreate = onCreate
        }
    }
    var glitterPage = GlitterPage(baseRout, updateRout, appName, onCreate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)
        showGlitterPage()
    }

    fun showGlitterPage(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.glitterFrag, glitterPage, "GlitterPage").commit()
        fn_video()
        instance = { this }
    }
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == 1 && event.keyCode == 4) {
            glitterPage.goBack()
            return false
        } else {
            return glitterPage.keyEventListener(event)
        }
    }

    fun fn_video() {
        val int_position = 0
        val uri: Uri
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val column_id: Int
        val thum: Int
        var absolutePathOfImage: String? = null
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Thumbnails.DATA
        )
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        cursor = applicationContext.contentResolver
            .query(uri, projection, null, null, "$orderBy DESC")!!
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            Log.e("Column", absolutePathOfImage)
            Log.e("Folder", cursor.getString(column_index_folder_name))
            Log.e("column_id", cursor.getString(column_id))
            Log.e("thum", cursor.getString(thum))
            Log.e("path", absolutePathOfImage)
        }
    }

    override fun onResume() {
        super.onResume()
        GlitterExecute.execute("lifeCycle.onResume()", ValueCallback{ a:String->})
    }
    override fun onPause() {
        super.onPause()
        GlitterExecute.execute("lifeCycle.onPause()", ValueCallback{ a:String->})
    }

    override fun onDestroy() {
        val mWebView=glitterPage.webRoot
        if( glitterPage.webRoot!=null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            val parent = mWebView.getParent() ;
            if (parent != null) {
                (parent as ViewGroup).removeView(mWebView);
            }

            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.settings.javaScriptEnabled = false;
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
    }



}