package com.example.bubblechatkotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent: Intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        }
        else {
            initializeView()
        }
    }

    private fun initializeView() {
        findViewById<Button>(R.id.button_start).setOnClickListener {
            startService(Intent(this, FloatingViewService::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if(resultCode == Activity.RESULT_OK) {
                initializeView()
            }
            else {
                Toast.makeText(this, "Permission not available.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}