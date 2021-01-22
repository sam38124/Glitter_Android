package com.jianzhi.glitter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.jztaskhandler.TaskHandler
import com.example.jztaskhandler.runner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jianzhi.glitter.VideoEnabledWebChromeClient.ToggledFullscreenCallback
import com.jianzhi.glitter.util.GpsUtil
import com.jianzhi.glitter.util.ZipUtil.getRequest
import com.jianzhi.glitter.util.ZipUtil.unzip
import com.jianzhi.jzblehelper.BleHelper
import com.jianzhi.jzblehelper.callback.BleCallBack
import com.jianzhi.jzblehelper.callback.ConnectResult
import com.jianzhi.jzblehelper.models.BleBinary
import com.jzsql.lib.mmySql.JzSqlHelper
import com.jzsql.lib.mmySql.Sql_Result
import com.orange.glitter.R
import com.orango.electronic.jzutil.getWebResource
import kotlinx.android.synthetic.main.glitter_page.view.*
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets


data class JsInterFace(var interFace: Any, var tag: String)
object GlitterExecute {
    var execute: (data: String, callback: ValueCallback<String>) -> Unit =
        { data: String, valueCallback: ValueCallback<String> -> }
}

class GlitterPage(
    var baseRout: String,
    var updateRout: String? = null,
    var appName: String,
    var onCreate: (GlitterPage) -> Unit = {}
) : Fragment(),
    BleCallBack {
    private val FILE_CHOOSER_RESULT_CODE = 10000
    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessageAboveL: ValueCallback<Array<Uri?>>? = null
    private var handler = Handler()
    lateinit var webRoot: WebView

    companion object {
        var interFace: Array<JsInterFace> = arrayOf()
        fun addJsInterFace(objects: Array<JsInterFace>) {
            interFace = objects
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    var ginterFace = GlitterInterFace()
    lateinit var rootview: View
    private var webChromeClient: VideoEnabledWebChromeClient? = null
    var onUpdate = false

    @SuppressLint("JavascriptInterface")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview = inflater.inflate(R.layout.glitter_page, container, false)
        dataMap["Glitter"]=JzSqlHelper(activity!!, "Glitter.db")
        for (i in interFace) {
            rootview.webroot.addJavascriptInterface(i.interFace, i.tag)
        }
        webRoot = rootview.webroot
        rootview.webroot.addJavascriptInterface(BleInterFace(), "GL_ble")
        rootview.webroot.addJavascriptInterface(ginterFace, "GL")
        rootview.webroot.addJavascriptInterface(Database(), "DataBase")
        rootview.webroot.settings.allowUniversalAccessFromFileURLs = true
        rootview.webroot.settings.javaScriptEnabled = true
        if (ginterFace.getPro("version") == null || updateRout == null) {
            rootview.webroot.loadUrl("$baseRout/glitterBundle/Application.html")
        } else {
            baseRout = context!!.applicationContext.filesDir.toString()
            rootview.webroot.loadUrl(
                "${Uri.fromFile(
                    File(
                        "$baseRout/glitterBundle",
                        "Application.html"
                    )
                )}"
            )
        }
        rootview.webroot.settings.pluginState = WebSettings.PluginState.ON;
        rootview.webroot.settings.pluginState = WebSettings.PluginState.ON_DEMAND;
        rootview.webroot.settings.javaScriptCanOpenWindowsAutomatically = true
        rootview.webroot.settings.setSupportMultipleWindows(true)
        GlitterExecute.execute = { data: String, valueCallback: ValueCallback<String> ->
            rootview.webroot.evaluateJavascript(data, valueCallback)
        }
        rootview.webroot.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                Log.e("OverrideUrlLoading", url)

                return false
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                rootview.webroot.evaluateJavascript(
                    "glitter.baseUrl='${this@GlitterPage.baseRout}/';glitter.type = appearType.Android;onCreate();",
                    null
                )
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

        if (updateRout != null) {
            TaskHandler.newInstance.runTaskTimer(lifecycle, 1000, 5000, runner {
                if (onUpdate) {
                    return@runner
                }
                Thread {
                    onUpdate = true
                    val version = "$updateRout/GlitterVersion?AppName=$appName".getWebResource(10000)
                    if (version == ginterFace.getPro("version") || version == null) {
                        onUpdate = false
                        return@Thread
                    }
                    val applicationContext = context!!.applicationContext
                    val logFile = File(applicationContext.filesDir, "web.zip")
                    if (getRequest(
                            "$updateRout/GetGlitter?AppName=$appName&version=$version",
                            1000 * 120,
                            file = logFile
                        )
                    ) {
                        Log.e("logFile", logFile.readText())
                        unzip(
                            File(applicationContext.filesDir, "web.zip"),
                            applicationContext.filesDir.path
                        )
                        for (i in (applicationContext.filesDir).listFiles()) {
                            Log.e("webRoot", i.path)
                        }
                        handler.post {
                            rootview.webroot.clearCache(true)
                            ginterFace.setPro("version", "$version");
//                          rootview.webroot.loadUrl("${Uri.fromFile(File("${applicationContext.filesDir}/glitterBundle", "Application.html"))}")
                            activity!!.recreate()
                            onUpdate = false
                        }
                    } else {
                        onUpdate = false
                    }
                }.start()
            })
        }

        onCreate(this)
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.e("goback", "goback")
                }
            })

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
                openImageChooserActivity()
            }

            // For Android  >= 3.0
            fun openFileChooser(valueCallback: ValueCallback<Uri>, acceptType: String?) {
                uploadMessage = valueCallback
                Log.e("acceptType", "acceptType--" + acceptType)
                openImageChooserActivity()
            }

            //For Android  >= 4.1
            fun openFileChooser(
                valueCallback: ValueCallback<Uri>,
                acceptType: String?,
                capture: String?
            ) {
                uploadMessage = valueCallback
                Log.e("acceptType", "acceptType--" + acceptType)
                openImageChooserActivity()
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
                openImageChooserActivity(fileChooserParams)
                return true
            }
        }
        webChromeClient!!.setOnToggledFullscreen(ToggledFullscreenCallback { fullscreen -> // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            if (fullscreen) {
                val attrs: WindowManager.LayoutParams = activity!!.getWindow().getAttributes()
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                activity!!.window.attributes = attrs
                if (Build.VERSION.SDK_INT >= 14) {
                    activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                }
            } else {
                val attrs: WindowManager.LayoutParams = activity!!.getWindow().getAttributes()
                attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                attrs.flags =
                    attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                activity!!.getWindow().setAttributes(attrs)
                if (Build.VERSION.SDK_INT >= 14) {
                    activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        })
        rootview.webroot.webChromeClient = webChromeClient;
        return rootview
    }

    fun keyEventListener(event: KeyEvent): Boolean {
        rootview.webroot.evaluateJavascript(
            "glitter.keyEventListener(JSON.parse('${Gson().toJson(
                event
            )}'));", null
        )
        return false
    }

    fun goBack() {
        rootview.webroot.evaluateJavascript("glitter.onBackPressed();", null)
    }

    // 2.回調方法觸發本地選擇文件
    private fun openImageChooserActivity(fileChooserParams: WebChromeClient.FileChooserParams? = null) {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        if (fileChooserParams != null) {
            if (fileChooserParams.acceptTypes.isNotEmpty()) {
                i.type = fileChooserParams.acceptTypes[0]
            } else {
                i.type = "image/*";//圖片上傳
            }
        } else {
            i.type = "image/*";//圖片上傳
        }

        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE)
    }

    // 3.選擇圖片後處理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(result)
                uploadMessage = null
            }
        } else {
            //這裏uploadMessage跟uploadMessageAboveL在不同系統版本下分別持有了
            //WebView對象，在用戶取消文件選擇器的情況下，需給onReceiveValue傳null返回值
            //否則WebView在未收到返回值的情況下，無法進行任何操作，文件選擇器會失效
            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(null)
                uploadMessage = null
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL!!.onReceiveValue(null)
                uploadMessageAboveL = null
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
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
    inner class GlitterInterFace() {
        var gpsUtil: GpsUtil? = null

        @JavascriptInterface
        fun getPro(tag: String): String? {
            val profilePreferences = context!!.getSharedPreferences("Setting", Context.MODE_PRIVATE)
            return profilePreferences.getString(tag, null)
        }

        @JavascriptInterface
        fun setPro(key: String, value: String) {
            val profilePreferences = context!!.getSharedPreferences("Setting", Context.MODE_PRIVATE)
            profilePreferences.edit().putString(key, value).commit()
        }

        @JavascriptInterface
        fun closeApp() {
            activity!!.finishAffinity();
        }

        @JavascriptInterface
        fun openQrScanner() {
            val intent = Intent(activity, ScannerActivity::class.java)
            activity!!.startActivity(intent)
        }

        @JavascriptInterface
        fun requestPermission(permission: Array<String>) {
            ActivityCompat.requestPermissions(activity!!, permission, 1022)
        }

        @JavascriptInterface
        fun getGPS(): String {
            if (gpsUtil == null) {
                gpsUtil = GpsUtil(this@GlitterPage)
            }
            val map: MutableMap<String, Any?> = mutableMapOf()
            map["latitude"] = gpsUtil!!.lastKnownLocation?.latitude
            map["longitude"] = gpsUtil!!.lastKnownLocation?.longitude
            map["address"] = gpsUtil!!.address
            return Gson().toJson(map)
        }
        var canPlayMedia=true
        @JavascriptInterface
        fun playSound(rout:String){
            if(!canPlayMedia){return}
            canPlayMedia=false

            if(baseRout.contains("file:///android_asset")){

                val assetRout="${baseRout.replace("file:///android_asset/","")}/${rout.replace("file:/android_asset/","")}"
                Log.e("assetRout",assetRout)
//                activity!!.assets.list("")
                val afd = activity!!.assets.openFd(assetRout);
                val mediiaplay = MediaPlayer()
                mediiaplay.setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
                mediiaplay.prepare()
                mediiaplay.setOnCompletionListener {
                    mediiaplay.release()
                    canPlayMedia=true
                }
                mediiaplay.start()
            }else{
                val  file= File("$baseRout/$rout")
                val mediiaplay = MediaPlayer.create(activity!!,Uri.fromFile(file))
                mediiaplay.setOnCompletionListener {
                    mediiaplay.release()
                    canPlayMedia=true
                }
                mediiaplay.start()
            }


        }
        @JavascriptInterface
        fun requestGPSPermission(){
            if(gpsUtil==null){gpsUtil= GpsUtil(this@GlitterPage)
            }

        }
    }

    /*
    * Ble開發套件
    * */
     var bleHelper: BleHelper? =null

    inner class BleInterFace {
        @JavascriptInterface
        fun startScan(): Boolean {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            return bleHelper!!.startScan()
        }

        @JavascriptInterface
        fun stopScan() {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            bleHelper!!.stopScan()
        }

        @JavascriptInterface
        fun writeHex(data: String, rx: String, tx: String) {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            bleHelper!!.writeHex(data, rx, tx)
        }

        @JavascriptInterface
        fun writeUtf(data: String, rx: String, tx: String) {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            bleHelper!!.writeUtf(data, rx, tx)
        }

        @JavascriptInterface
        fun writeBytes(data: ByteArray, rx: String, tx: String) {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            bleHelper!!.writeBytes(data, rx, tx)
        }

        @JavascriptInterface
        fun start() {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}

        }

        @JavascriptInterface
        fun connect(device: String, sec: Int,id:Int) {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            bleHelper!!.connect(device, sec, ConnectResult {
                Thread{
                    Thread.sleep(1000)
                    handler.post {
                        webRoot.evaluateJavascript("  glitter.callBackList.get($id)($it);" +
                                "glitter.callBackList.delete($id);",null)
                    }
                }.start()

            })
        }

        @JavascriptInterface
        fun isOpen(): Boolean {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            return bleHelper!!.bleadapter.isEnabled
        }

        @JavascriptInterface
        fun gpsEnable(): Boolean {
            val locationManager =
                context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return gps || network
        }
        @JavascriptInterface
        fun isDiscovering(): Boolean {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            return bleHelper!!.bleadapter.isDiscovering
        }
        @JavascriptInterface
        fun disConnect() {
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            return bleHelper!!.disconnect()
        }
        @JavascriptInterface
        fun isConnect():Boolean{
            if(bleHelper==null){ bleHelper = BleHelper(activity!!, this@GlitterPage)}
            return bleHelper!!.isConnect()
        }
    }

    override fun needGPS() {
        handler.post { webRoot.evaluateJavascript("glitter.bleUtil.callback.needGPS()", null) }
    }

    override fun onConnectFalse() {
        handler.post { webRoot.evaluateJavascript("glitter.bleUtil.callback.onConnectFalse()", null) }
    }

    override fun onConnectSuccess() {
        handler.post { webRoot.evaluateJavascript("glitter.bleUtil.callback.onConnectSuccess()", null) }
    }

    override fun onConnecting() {
        handler.post { webRoot.evaluateJavascript("glitter.bleUtil.callback.onConnecting()", null) }
    }

    override fun onDisconnect() {
        handler.post { webRoot.evaluateJavascript("glitter.bleUtil.callback.onDisconnect()", null) }
    }

    override fun requestPermission(permission: ArrayList<String>) {
        //當藍牙權限不足時觸發
        for (i in permission) {
            Log.e("JzBleMessage", "權限不足請先請求權限${i}")
        }

        handler.post {
            webRoot.evaluateJavascript(
                "glitter.bleUtil.callback.requestPermission(${Gson().toJson(
                    permission
                )})", null
            )
        }
    }

    override fun rx(a: BleBinary) {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["readHEX"] = a.readHEX()
        map["readBytes"] = a.readBytes()
        map["readUTF"] = a.readUTF()
        handler.post {
            webRoot.evaluateJavascript(
                "glitter.bleUtil.callback.rx(" + Gson().toJson(map) + ")",
                null
            )
        }
    }

    override fun scanBack(device: BluetoothDevice, scanRecord: BleBinary) {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["name"] = if (device.name == null) "undefine" else device.name
        map["address"] = device.address
        val rec: MutableMap<String, Any> = mutableMapOf()
        rec["readHEX"] = scanRecord.readHEX().substring(34)
        rec["readBytes"] = scanRecord.readBytes().copyOfRange(17, scanRecord.readBytes().size)
        rec["readUTF"] = String(rec["readBytes"] as ByteArray, StandardCharsets.UTF_8);
        handler.post {
            webRoot.evaluateJavascript(
                "glitter.bleUtil.callback.scanBack(" + Gson().toJson(map) + "," + Gson().toJson(
                    rec
                ) + ")", null
            )
        }
    }

    override fun tx(b: BleBinary) {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["readHEX"] = b.readHEX()
        map["readBytes"] = b.readBytes()
        map["readUTF"] = b.readUTF()
        handler.post {
            webRoot.evaluateJavascript(
                "glitter.bleUtil.callback.tx(" + Gson().toJson(map) + ")",
                null
            )
        }
    }

/*
* DataBase開發套件
* */
    var dataMap:MutableMap<String,JzSqlHelper> = mutableMapOf()
    inner class Database{
        @JavascriptInterface
        fun exSql(name:String,string:String){
            dataMap[name]!!.exsql(string)
        }
        @JavascriptInterface
        fun query(name:String,string: String):String{
            val mapArray:ArrayList<MutableMap<String,Any>> = arrayListOf()
            dataMap[name]!!.query(string, Sql_Result {
                val map:MutableMap<String,Any> = mutableMapOf()
                for(a in 0 until it.columnCount){
                    map[it.getColumnName(a)]=it.getString(a)
                }
                mapArray.add(map)
            })
            Log.e("DataBase",Gson().toJson(mapArray))
            return Gson().toJson(mapArray)
        }
        @JavascriptInterface
        fun initByFile(name:String,rout:String){
            if(dataMap[name]==null){
                dataMap[name]=JzSqlHelper(activity!!,name)
            }
            dataMap[name]!!.close()
            if(baseRout.contains("file:///android_asset")){
                val assetRout="${baseRout.replace("file:///android_asset/","")}/${rout.replace("file:/android_asset/","")}"
                Log.e("assetRout",assetRout)
//                activity!!.assets.list("")
                dataMap[name]!!.dbinit(activity!!.assets.open(assetRout))
                dataMap[name]!!.create()
            }else{
                val  file= File("$baseRout/$rout")
                dataMap[name]!!.dbinit(file.inputStream())
                dataMap[name]!!.create()
            }

        }
    }
}

