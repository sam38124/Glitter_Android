package com.jianzhi.glitter

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.DiaClass
import com.orange.jzchi.jzframework.JzActivity
import kotlinx.android.synthetic.main.dialog_main.*


class MainActivity : JzActivity() {
    override fun changePageListener(tag: String, frag: Fragment) {
//        webroot.evaluateJavascript("JzActivity.changePageListener()", null)
    }

    override fun dialogLinstener(dialog: DiaClass, tag: String) {
//        if (dialog.callback is BottomSheetDialog) {
//            webroot.evaluateJavascript(
//                "JzActivity.dialogListener('${tag}','${(dialog.callback as BottomSheetDialog).url}')",
//                null
//            )
//        } else {
//            webroot.evaluateJavascript(
//                "JzActivity.dialogListener('${tag}','${(dialog.callback as BottomSheetDialog).url}')",
//                null
//            )
//        }
    }

    override fun keyEventListener(event: KeyEvent): Boolean {
//        webroot.evaluateJavascript("JzActivity.keyEventListener(JSON.parse('${Gson().toJson(event)}'))", null)
        return true
    }


    override fun savedInstanceAble(): Boolean {
        return true
    }

    override fun onResume() {
//        webroot.evaluateJavascript(
//            "JzActivity.onResume()",
//            null
//        )
        super.onResume()
    }

    override fun onDestroy() {

        super.onDestroy()
    }
    override fun onPause() {
        super.onPause()
    }

    override fun viewInit(rootview: View) {
Thread{
    handler.post {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//      GlitterActivity.setUp("http://192.168.43.219/Glitter/Zhexing/appData/","http://192.168.43.219","Zhexing")
        GlitterActivity.setUp("file:///android_asset/Zhexing/appData",appName = "Zhexing")
//        GlitterActivity.setUp("file:///android_asset/appData",appName = "appData")
//        GlitterActivity.setUp("http://192.168.43.219/Glitter/tsport/appData/",appName = "tsport",updateRout = "http://192.168.43.219")
        val intent=Intent(this,GlitterActivity::class.java)
        this.startActivity(intent)
//        val intent=Intent(this,ScannerActivity::class.java)
//        this.startActivity(intent)
//        getControlInstance().setHome(GlitterPage("https://www.cssscript.com/demo/material-bottom-sheet/","MainActivity"),"MainActivity")
//        getControlInstance().setHome(GlitterPage("https://www.cssscript.com/demo/mobile-first-drawer-navigation-vanilla-javascript-hy-drawer",""),"MainActivity")
    }
}.start()

    }


}