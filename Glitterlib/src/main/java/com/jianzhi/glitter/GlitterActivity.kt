package com.jianzhi.glitter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.example.jztaskhandler.TaskHandler
import com.example.jztaskhandler.runner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jianzhi.glitter.util.GpsUtil
import com.jianzhi.glitter.util.ZipUtil
import com.jianzhi.glitter.util.downloadFile
import com.jzsql.lib.mmySql.JzSqlHelper
import com.orange.glitter.R
import com.orango.electronic.jzutil.getWebResource
import com.orango.electronic.jzutil.toHex
import kotlinx.android.synthetic.main.glitter_page.view.*
import java.io.File


data class JsInterFace(var interFace: Any, var tag: String)
object GlitterExecute {
    var execute: (data: String, callback: ValueCallback<String>) -> Unit =
        { data: String, valueCallback: ValueCallback<String> -> }
}

class GlitterActivity : AppCompatActivity(), CameraXConfig.Provider {
   
    public var uploadMessage: ValueCallback<Uri>? = null
    public var uploadMessageAboveL: ValueCallback<Array<Uri?>>? = null
    public var handler = Handler()
    lateinit var webRoot: WebView
    companion object {
        private val FILE_CHOOSER_RESULT_CODE = 10000
        var webviewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                Log.e("OverrideUrlLoading", url)
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                Log.e("shouldInterceptRequest", "" + request.url)

                return super.shouldInterceptRequest(view, request)
            }
        }
        var baseRout: String = ""
        var updateRout: String? = null
        var appName: String = ""
        lateinit var instance: () -> GlitterActivity
        fun setUp(
            baseRout: String,
            updateRout: String? = null,
            appName: String
        ) {
            this.appName = appName
            this.baseRout = baseRout
            this.updateRout = updateRout
        }

        //自定義函式
        var javaScriptInterFace: ArrayList<JavaScriptInterFace> = arrayListOf()
        fun addJavacScriptInterFace(myInterface: JavaScriptInterFace) {
            javaScriptInterFace.add(myInterface)
        }
        //添加自定義Activity回調
        var activityResultList:ArrayList<ResultCallBack> = ArrayList()
        fun addActivityResult(callback:ResultCallBack){
            activityResultList.add(callback)
        }
        //添加創建回條
        var onCreateCallBack:() -> Unit = { }
        //自定義圖片選擇回條
        var setImageCallBack:(fileChooserParams: WebChromeClient.FileChooserParams?)->Unit  = {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            instance().startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE)
        }
    }
    //Activity回調
    interface ResultCallBack{
       abstract fun resultBack(requestCode: Int, resultCode: Int, data: Intent?)
    }

    var ginterFace = GlitterInterFace()
    private var webChromeClient: VideoEnabledWebChromeClient? = null
    var onUpdate = false
    lateinit var rootview: View

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateCallBack()
        /** PictureSelector日志管理配制开始  */
        /** PictureSelector日志管理配制结束  */
        setContentView(R.layout.glitter_page)
        rootview = findViewById<View>(android.R.id.content).rootView
        window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dataMap["Glitter"] = JzSqlHelper(this, "Glitter.db")
        webRoot = rootview.webroot
        rootview.webroot.settings.domStorageEnabled=true
        rootview.webroot.addJavascriptInterface(ginterFace, "GL")
        rootview.webroot.addJavascriptInterface(Database(), "DataBase")
        rootview.webroot.settings.allowUniversalAccessFromFileURLs = true
        HeightVisibleChangeListener(rootview.webroot);
        rootview.webroot.settings.javaScriptEnabled = true
        rootview.webroot.loadUrl("$baseRout/home.html")
        Log.e("asseturl", "" + Uri.fromFile(File("file:///android_asset/appData/glitterBundle/Application.html")))
        // file:///android_asset/appData/glitterBundle/Application.html#
        rootview.webroot.settings.pluginState = WebSettings.PluginState.ON_DEMAND;
        rootview.webroot.settings.javaScriptCanOpenWindowsAutomatically = true
        rootview.webroot.settings.setSupportMultipleWindows(true)
        rootview.webroot.settings.setAppCacheEnabled(true)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        GlitterExecute.execute = { data: String, valueCallback: ValueCallback<String> ->
            rootview.webroot.evaluateJavascript(data, valueCallback)
        }
        rootview.webroot.webViewClient = webviewClient
        // Initialize the VideoEnabledWebChromeClient and set event handlers
        val nonVideoLayout: View = rootview.nonVideoLayout // Your own view, read class comments

        val videoLayout = rootview.videoLayout

        //noinspection all
        val loadingView: View = layoutInflater.inflate(
            R.layout.view_loading_video,
            null
        ) // Your own view, read class comments

        webChromeClient = object : VideoEnabledWebChromeClient(
            nonVideoLayout,
            videoLayout,
            loadingView,
            rootview.webroot // See all available constructors...
        ) {
            // Subscribe to standard events, such as onProgressChanged()...
            override fun onProgressChanged(view: WebView, progress: Int) {
                // Your code...
            }

            fun openFileChooser(valueCallback: ValueCallback<Uri>) {
                uploadMessage = valueCallback
                setImageCallBack(null)
            }

            // For Android  >= 3.0
            fun openFileChooser(valueCallback: ValueCallback<Uri>, acceptType: String?) {
                uploadMessage = valueCallback
                Log.e("acceptType", "acceptType--" + acceptType)
                setImageCallBack(null)
            }

            //For Android  >= 4.1
            fun openFileChooser(
                valueCallback: ValueCallback<Uri>,
                acceptType: String?,
                capture: String?
            ) {
                uploadMessage = valueCallback
                Log.e("acceptType", "acceptType--" + acceptType)
                setImageCallBack(null)
            }

            // For Android >= 5.0
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {
                Log.e(
                    "onShowFileChooser",
                    "onShowFileChooser${Gson().toJson(fileChooserParams.acceptTypes)}"
                )
                uploadMessageAboveL = filePathCallback
                setImageCallBack(fileChooserParams)
                return true
            }
        }
        webChromeClient!!.setOnToggledFullscreen { fullscreen -> // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            if (fullscreen) {
                val attrs: WindowManager.LayoutParams = this.getWindow().getAttributes()
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                this.window.attributes = attrs
                if (Build.VERSION.SDK_INT >= 14) {
                    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                }
            } else {
                val attrs: WindowManager.LayoutParams = this.getWindow().getAttributes()
                attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                attrs.flags =
                    attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                this.window.attributes = attrs
                if (Build.VERSION.SDK_INT >= 14) {
                    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }
        rootview.webroot.webChromeClient = webChromeClient;
//        fn_video()
        instance = { this }
        GlitterFunction.create()
    }

    fun keyEventListener(event: KeyEvent): Boolean {
        rootview.webroot.evaluateJavascript(
            "glitter.keyEventListener(JSON.parse('${
                Gson().toJson(
                    event
                )
            }'));", null
        )
        return false
    }

    fun goBack() {
        rootview.webroot.evaluateJavascript("glitter.onBackPressed();", null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultList.map { it.resultBack(requestCode,resultCode,data) }
        Log.e("requestBack","requestCode:${requestCode}-resultCode:${resultCode}-data:${data}")
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null) return
        var results: Array<Uri?>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = arrayOfNulls(clipData.itemCount)
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        results[i] = item.uri
                    }
                }
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
            }
        }
        uploadMessageAboveL!!.onReceiveValue(results)
        uploadMessageAboveL = null
    }

    /*
    * Glitter 開發套件
    * */
    inner class GlitterInterFace {
        var gpsUtil: GpsUtil? = null

        @JavascriptInterface
        fun downloadFile(serverRout: String, fileName: String, callbackID: Int, timeOut: Int) {
            Thread {
                Log.e("downloadFile", "start:${serverRout}")
                val data = serverRout.downloadFile(timeOut, fileName)
                handler.post {
                    rootview.webroot.evaluateJavascript("glitter.callBackList.get(${callbackID})(${data});", null)
                }
                if (!data) { Log.e("downloadFile", "False-${fileName}") }
            }.start()
        }

        @JavascriptInterface
        fun getFile(fileName: String, type: String, callbackID: Int) {
            Thread {
                var script = "glitter.callBackList.get(${callbackID})(undefined)"
                try {
                    when (type) {
                        "hex" -> {
                            script = "glitter.callBackList.get(${callbackID})('${
                                File(
                                    instance().applicationContext.filesDir,
                                    fileName
                                ).readBytes().toHex()
                            }');"
                        }
                        "bytes" -> {
                            script = "glitter.callBackList.get(${callbackID})(${
                                Gson().toJson(
                                    File(
                                        instance().applicationContext.filesDir,
                                        fileName
                                    ).readBytes()
                                )
                            });"
                        }
                        "text" -> {
                            script = "glitter.callBackList.get(${callbackID})('${
                                Gson().toJson(
                                    File(
                                        instance().applicationContext.filesDir,
                                        fileName
                                    ).readText()
                                )
                            }');"
                        }
                    }
                } catch (e: Exception) {

                }

                handler.post {
                    rootview.webroot.evaluateJavascript(script, null)
                }
                Log.e("getFile", "result-$script")
            }.start()
        }

        @JavascriptInterface
        fun checkFileExists(fileName: String, callbackID: Int) {
            Thread {
                val script = "glitter.callBackList.get(${callbackID})(${
                    File(
                        instance().applicationContext.filesDir,
                        fileName
                    ).exists()
                })"
                handler.post {
                    rootview.webroot.evaluateJavascript(script, null)
                }
                Log.e("getFile", "result-$script")
            }.start()
        }

        @JavascriptInterface
        fun toAssetRoot(rout: String) {
            handler.post {
                recreate()
            }
        }

        @JavascriptInterface
        fun reloadPage() {
            handler.post { recreate() }
        }

        @JavascriptInterface
        fun openNewTab(link: String) {
            val intent = Intent(this@GlitterActivity, WebViewAct::class.java)
            intent.putExtra("url", link)
            startActivity(intent)
        }


        @JavascriptInterface
        fun closeApp() {
            this@GlitterActivity.finishAffinity();
        }


        @JavascriptInterface
        fun openQrScanner() {
            val intent = Intent(this@GlitterActivity, ScannerActivity::class.java)
            this@GlitterActivity.startActivity(intent)
        }

        @JavascriptInterface
        fun requestPermission(permission: Array<String>, callbackID: Int) {
            handler.post {
                var requestSuccess = 0
                getPermission(permission, object : permission_C {
                    override fun requestSuccess(a: String?) {
                        requestSuccess += 1
                        if (requestSuccess == permission.size) {
                            instance().webRoot.evaluateJavascript("glitter.callBackList.get(${callbackID})(true)", null)
                        }
                    }

                    override fun requestFalse(a: String?) {
                        instance().webRoot.evaluateJavascript("glitter.callBackList.get(${callbackID})(false)", null)
                    }
                })
            }
        }

        @JavascriptInterface
        fun getGPS(): String {
            if (gpsUtil == null) {
                gpsUtil = GpsUtil(this@GlitterActivity)
            }
            val map: MutableMap<String, Any?> = mutableMapOf()
            map["latitude"] = gpsUtil!!.lastKnownLocation?.latitude
            map["longitude"] = gpsUtil!!.lastKnownLocation?.longitude
            map["address"] = gpsUtil!!.address
            return Gson().toJson(map)
        }

        var canPlayMedia = true

        @JavascriptInterface
        fun playSound(rout: String) {
            if (!canPlayMedia) {
                return
            }
            canPlayMedia = false

            if (baseRout.contains("file:///android_asset")) {
                val assetRout =
                    "${baseRout.replace("file:///android_asset/", "")}/${rout.replace("file:/android_asset/", "")}"
                Log.e("assetRout", assetRout)
                val afd = this@GlitterActivity.assets.openFd(assetRout);
                val mediiaplay = MediaPlayer()
                mediiaplay.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                mediiaplay.prepare()
                mediiaplay.setOnCompletionListener {
                    mediiaplay.release()
                    canPlayMedia = true
                }
                mediiaplay.start()
            } else {
                val file = File("$baseRout/$rout")
                val mediiaplay = MediaPlayer.create(this@GlitterActivity, Uri.fromFile(file))
                mediiaplay.setOnCompletionListener {
                    mediiaplay.release()
                    canPlayMedia = true
                }
                mediiaplay.start()
            }


        }

        @JavascriptInterface
        fun requestGPSPermission() {
            if (gpsUtil == null) {
                gpsUtil = GpsUtil(this@GlitterActivity)
            }

        }
        @JavascriptInterface
        fun runJsInterFace(data: String) {
            Thread{
                val mapData =
                    Gson().fromJson<MutableMap<String, Any>>(data, object : TypeToken<MutableMap<String, Any>>() {}.type)
                val functionName = mapData["functionName"] as String
                val callbackID = (mapData["callBackId"] as Double).toInt()
                val receiveValue: MutableMap<String, Any> =
                    if (mapData["data"] == null) mutableMapOf() else (mapData["data"] as MutableMap<String, Any>)
                val cFunction = javaScriptInterFace.filter { it.functionName==functionName }
                val requestFunction = RequestFunction(receiveValue)
                requestFunction.finish={
                    handler.post {
                        instance().webRoot.evaluateJavascript("""
                glitter.callBackList.get(${callbackID})(${Gson().toJson(requestFunction.responseValue)});
                glitter.callBackList.delete(${callbackID});
                """.trimIndent(), null)
                    }
                }
                if(cFunction.isNotEmpty()){
                    cFunction[0].function(requestFunction)
                }else{
                    requestFunction.finish()
                }

            }.start()
        }
    }

    /*
    * DataBase開發套件
    * */
    var dataMap: MutableMap<String, JzSqlHelper> = mutableMapOf()

    inner class Database {
        @JavascriptInterface
        fun exSql(name: String, string: String) {
            if (dataMap[name] == null) {
                dataMap[name] = JzSqlHelper(this@GlitterActivity, name)
            }
            dataMap[name]!!.exsql(string)
        }

        @JavascriptInterface
        fun query(name: String, string: String): String {
            Log.e("sql", string)
            if (dataMap[name] == null) {
                dataMap[name] = JzSqlHelper(this@GlitterActivity, name)
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
            Log.e("DataBase", Gson().toJson(mapArray))
            return Gson().toJson(mapArray)
        }

        @JavascriptInterface
        fun initByFile(name: String, rout: String) {
            if (dataMap[name] == null) {
                dataMap[name] = JzSqlHelper(this@GlitterActivity, name)
            }
            dataMap[name]!!.close()
            if (baseRout.contains("file:///android_asset")) {
                val assetRout =
                    "${baseRout.replace("file:///android_asset/", "")}/${rout.replace("file:/android_asset/", "")}"
                Log.e("assetRout", assetRout)
//                this@GlitterActivity.assets.list("")
                dataMap[name]!!.dbinit(this@GlitterActivity.assets.open(assetRout))
                dataMap[name]!!.create()
            } else {
                val file = File("$baseRout/$rout")
                dataMap[name]!!.dbinit(file.inputStream())
                dataMap[name]!!.create()
            }
        }

        @JavascriptInterface
        fun initByLocalFile(name: String, rout: String) {
            if (dataMap[name] == null) {
                dataMap[name] = JzSqlHelper(this@GlitterActivity, name)
            }
            dataMap[name]!!.close()
            val file = File(applicationContext.filesDir, rout)
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
        }

        @JavascriptInterface
        fun init(name: String) {
            if (dataMap[name] == null) {
                dataMap[name] = JzSqlHelper(this@GlitterActivity, name)
            }
            dataMap[name]!!.close()
            dataMap[name]!!.create()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == 1 && event.keyCode == 4) {
            goBack()
            return false
        } else {
            return keyEventListener(event)
        }
    }

    //
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
        GlitterExecute.execute("lifeCycle.onResume()", ValueCallback { a: String -> })
    }

    override fun onPause() {
        super.onPause()
        GlitterExecute.execute("lifeCycle.onPause()", ValueCallback { a: String -> })
    }

    override fun onDestroy() {
        val mWebView = webRoot
        if (webRoot != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            val parent = mWebView.getParent();
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    var permissionRequestCode = 100
    var permissionCaller = object : permission_C {
        override fun requestSuccess(a: String?) {

        }

        override fun requestFalse(a: String?) {
        }
    }

     fun getPermission(Permissions: Array<String>, caller: permission_C) {
        permissionCaller = caller
        val permissionDeniedList = ArrayList<String>()
        for (permission in Permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                caller.requestSuccess(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(this, deniedPermissions, 100)
        }
    }

    interface permission_C {
        fun requestSuccess(a: String?)
        fun requestFalse(a: String?)
    }

    /**
     * 請求成功
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("ScannerActivity", "requestCode$requestCode")
        Log.e("ScannerActivity", "grantResults${Gson().toJson(grantResults)}")
        when (requestCode) {
            permissionRequestCode -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            permissionCaller.requestSuccess(permissions[i])
                        } else {
                            permissionCaller.requestFalse(permissions[i])
                        }
                    }
                }
            }

        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }



}

