package com.example.bubblechatkotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import kotlin.math.roundToInt


class FloatingViewService : Service() {
    lateinit var mWindowManager: WindowManager
    lateinit var mFloatingView: View
    lateinit var params: WindowManager.LayoutParams

    override fun onBind(intent: Intent) : IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        val type: Int = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_PHONE
        } else {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)

        val collapsedView: View  = mFloatingView.findViewById(R.id.collapse_view)
        val expandedView: View = mFloatingView.findViewById(R.id.expanded_container)

        val closeButtonCollapsed: ImageView = mFloatingView.findViewById(R.id.close_btn)
        closeButtonCollapsed.setOnClickListener {
            stopSelf()
        }

        val button1: Button = mFloatingView.findViewById(R.id.button1)
        button1.setOnClickListener {
            Toast.makeText(this, "BUTTON 1!", Toast.LENGTH_LONG).show()
        }

        val button2: Button = mFloatingView.findViewById(R.id.button2)
        button2.setOnClickListener {
            Toast.makeText(this, "BUTTON 2!", Toast.LENGTH_LONG).show()
        }

        val closeButton: ImageView = mFloatingView.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE
        }

        val openButon: ImageView = mFloatingView.findViewById(R.id.open_button)
        openButon.setOnClickListener {
            val appIntent: Intent = Intent(this, MainActivity::class.java)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(appIntent)

            stopSelf()
        }

        mFloatingView.findViewById<View>(R.id.root_container).setOnTouchListener (object: View.OnTouchListener {
            var initialX: Int? = null
            var initialY: Int? = null
            var initialTouchX: Float? = null
            var initialTouchY: Float? = null

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y

                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val Xdiff: Int = (event.rawX - initialTouchX!!).toInt()
                        val Ydiff: Int = (event.rawY - initialTouchY!!).toInt()

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.visibility = View.GONE
                                expandedView.visibility = View.VISIBLE
                            }
                        }

                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        println("initialX--- $initialX")
                        params.x = initialX!!.plus(event.rawX - initialTouchX!!).roundToInt()
                        params.y = initialY!!.plus(event.rawY - initialTouchY!!).roundToInt()

                        mWindowManager.updateViewLayout(mFloatingView, params)

                        return true
                    }
                }
                return false
            }
        })
    }

    private fun isViewCollapsed(): Boolean {
        return mFloatingView == null || mFloatingView.findViewById<View>(R.id.collapse_view).visibility == View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mFloatingView != null) {
            mWindowManager.removeView(mFloatingView)
        }
    }
}