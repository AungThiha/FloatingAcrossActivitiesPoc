package thiha.aung.foatingacrossactivitiespoc

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import kotlin.math.ceil

class FloatingWebViewService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: FrameLayout? = null
    private var hoverWebView: WebView? = null
    private val szWindow = Point()
    private var xDelta = 0
    private var yDelta = 0

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        windowManager?.let {
            it.defaultDisplay?.getSize(szWindow)
            val layoutParams =
                floatingView?.layoutParams as WindowManager.LayoutParams
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (layoutParams.y + (floatingView!!.height + statusBarHeight) > szWindow.y) {
                    layoutParams.y = szWindow.y - (floatingView?.height ?: 0 + statusBarHeight)
                    it.updateViewLayout(floatingView, layoutParams)
                }
            }
        }
    }

    private val statusBarHeight: Int
        get() = ceil(
            25 * applicationContext.resources.displayMetrics.density.toDouble()
        ).toInt()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return intent.getStringExtra("url")?.let {
            return if (startId == START_STICKY) {
                handleStart(it)
                super.onStartCommand(intent, flags, startId)
            } else {
                hoverWebView?.loadUrl(it)
                START_NOT_STICKY
            }
        } ?: START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let {
            windowManager?.removeView(it)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun handleStart(url: String) {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.floating_view, null) as FrameLayout
        hoverWebView = WebView(this)
        val hoverVideoViewLP = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        hoverWebView!!.layoutParams = hoverVideoViewLP
        floatingView!!.addView(hoverWebView)
        windowManager!!.defaultDisplay.getSize(szWindow)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val params = WindowManager.LayoutParams(
            640,
            360,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 30
        windowManager!!.addView(floatingView, params)
        floatingView!!.setOnTouchListener { view, event ->
            val xPosition = event.rawX.toInt()
            val yPosition = event.rawY.toInt()
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val lParams =
                        view.layoutParams as WindowManager.LayoutParams
                    xDelta = xPosition - lParams.x
                    yDelta = yPosition - lParams.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val layoutParams =
                        view.layoutParams as WindowManager.LayoutParams
                    var newX = xPosition - xDelta
                    var newY = yPosition - yDelta
                    if (newX < 0) {
                        newX = 0
                    } else if (newX + hoverWebView!!.width > szWindow.x) {
                        newX = szWindow.x - hoverWebView!!.width
                    }
                    if (newY < 0) {
                        newY = 0
                    } else if (newY + hoverWebView!!.height + statusBarHeight > szWindow.y) {
                        newY = szWindow.y - (hoverWebView!!.height + statusBarHeight)
                    }
                    layoutParams.x = newX
                    layoutParams.y = newY
                    windowManager!!.updateViewLayout(view, layoutParams)
                }
                else -> {
                }
            }
            true
        }
        // TODO may need to clean up from here.
        hoverWebView!!.loadUrl(url)
    }
}