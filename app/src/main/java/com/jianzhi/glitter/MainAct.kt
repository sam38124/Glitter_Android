package com.jianzhi.glitter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainAct: AppCompatActivity(){
    var handler=Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act)
        GlitterActivity.setUp("file:///android_asset/sample",appName = "sample")
            .onCreate {

            }
            .open(this,3000)

        findViewById<Button>(R.id.button).setOnClickListener {
            Toast.makeText(this,"23",Toast.LENGTH_SHORT).show()
        }
    }
}