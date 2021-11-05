package com.jianzhi.glitter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.orange.glitter.R
import kotlinx.android.synthetic.main.glitter_page.view.*


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
        var parameter:String? = null
        lateinit var instance: () -> GlitterActivity
        fun setUp(
            baseRout: String,
            updateRout: String? = null,
            appName: String,
            parameter: String? = null
        ) {
            this.appName = appName
            this.baseRout = baseRout
            this.updateRout = updateRout
            this.parameter = parameter
        }

        //自定義ＪＳ函式
        var javaScriptInterFace: ArrayList<JavaScriptInterFace> = arrayListOf()
        fun addJavacScriptInterFace(myInterface: JavaScriptInterFace) {
            javaScriptInterFace=ArrayList(javaScriptInterFace.filter {
                it.functionName!=myInterface.functionName
            }.toList())
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
       abstract fun resultBack(requestCode: Int, resultCode: Int, data: Intent)
    }

    var ginterFace = GlitterInterFace()
    private var webChromeClient: VideoEnabledWebChromeClient? = null
    var onUpdate = false
    lateinit var rootview: View

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** PictureSelector日志管理配制开始  */
        /** PictureSelector日志管理配制结束  */
        setContentView(R.layout.glitter_page)
        rootview = findViewById<View>(android.R.id.content).rootView
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        webRoot = rootview.webroot
        rootview.webroot.settings.domStorageEnabled=true
        rootview.webroot.addJavascriptInterface(ginterFace, "GL")
        rootview.webroot.settings.allowUniversalAccessFromFileURLs = true
        HeightVisibleChangeListener(rootview.webroot);
        rootview.webroot.settings.javaScriptEnabled = true
        rootview.webroot.loadUrl("$baseRout/home.html${parameter ?: ""}")
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
        instance = { this }
        onCreateCallBack()
    }

    fun keyEventListener(event: KeyEvent): Boolean {
        rootview.webroot.evaluateJavascript(
            "glitter.keyEventListener(JSON.parse('${
                Gson().toJson(event)
            }'));", null
        )
        return false
    }

    fun goBack() {
        rootview.webroot.evaluateJavascript("glitter.onBackPressed();", null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultList.map { it.resultBack(requestCode,resultCode,data!!) }
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





    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == 1 && event.keyCode == 4) {
            goBack()
            return false
        } else {
            return keyEventListener(event)
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
        override fun requestSuccess(a: String) {

        }

        override fun requestFalse(a: String) {
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
        fun requestSuccess(a: String)
        fun requestFalse(a: String)
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

