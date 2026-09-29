package com.example.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.system.DeviceInfoManager
import com.example.system.PerformanceManager
import kotlinx.coroutines.*
import kotlin.math.cos
import kotlin.math.sin

class FloatingMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var windowManager: WindowManager? = null

    private var hudView: View? = null
    private var crosshairView: View? = null

    private var hudParams: WindowManager.LayoutParams? = null
    private var crosshairParams: WindowManager.LayoutParams? = null

    private var isMinimized = false

    companion object {
        const val CHANNEL_ID = "nexa_floating_channel"
        const val NOTIFICATION_ID = 8802
        const val ACTION_START_HUD = "ACTION_START_HUD"
        const val ACTION_START_CROSSHAIR = "ACTION_START_CROSSHAIR"
        const val ACTION_STOP_ALL = "ACTION_STOP_ALL"

        fun startHud(context: Context) {
            val intent = Intent(context, FloatingMonitorService::class.java).apply {
                action = ACTION_START_HUD
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun startCrosshair(context: Context) {
            val intent = Intent(context, FloatingMonitorService::class.java).apply {
                action = ACTION_START_CROSSHAIR
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingMonitorService::class.java).apply {
                action = ACTION_STOP_ALL
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        PerformanceManager.startFpsMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALL) {
            removeViews()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification())

        if (intent?.action == ACTION_START_CROSSHAIR) {
            setupCrosshairView()
        } else {
            setupHudView()
        }

        startTelemetryLoop()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Overlay HUD",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for the on-screen gaming overlay and crosshair"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, FloatingMonitorService::class.java).apply {
            action = ACTION_STOP_ALL
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NEXA VORTEX • FLOATING OVERLAY")
            .setContentText("Gaming HUD & Crosshair active")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close Overlay", stopPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupHudView() {
        if (hudView != null) return

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        hudParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 60
            y = 180
        }

        // Programmatically build Cyber HUD Card
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 16, 24, 16)
            background = createCyberHudBackground()
            elevation = 12f
        }

        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val titleTv = TextView(this).apply {
            text = "NEXA VORTEX"
            setTextColor(Color.parseColor("#FF2A4D"))
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.1f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val minBtn = TextView(this).apply {
            text = "━ "
            setTextColor(Color.WHITE)
            textSize = 12f
            setPadding(12, 0, 12, 0)
            setOnClickListener { toggleMinimize() }
        }

        val closeBtn = TextView(this).apply {
            text = "✕"
            setTextColor(Color.parseColor("#FF4081"))
            textSize = 12f
            setPadding(12, 0, 8, 0)
            setOnClickListener {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        headerRow.addView(titleTv)
        headerRow.addView(minBtn)
        headerRow.addView(closeBtn)
        root.addView(headerRow)

        val statsTv = TextView(this).apply {
            id = View.generateViewId()
            tag = "STATS_TV"
            text = "FPS: 60 | RAM: 3.2G\nTEMP: 39°C | BAT: 85%"
            setTextColor(Color.parseColor("#ECEFF4"))
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(0, 10, 0, 0)
        }
        root.addView(statsTv)

        // Make draggable
        root.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = hudParams?.x ?: 0
                        initialY = hudParams?.y ?: 0
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        hudParams?.x = initialX + (event.rawX - initialTouchX).toInt()
                        hudParams?.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(root, hudParams)
                        return true
                    }
                }
                return false
            }
        })

        hudView = root
        try {
            windowManager?.addView(hudView, hudParams)
        } catch (_: Exception) {}
    }

    private fun toggleMinimize() {
        val root = hudView as? LinearLayout ?: return
        val statsView = root.findViewWithTag<View>("STATS_TV") ?: return
        isMinimized = !isMinimized
        statsView.visibility = if (isMinimized) View.GONE else View.VISIBLE
    }

    private fun setupCrosshairView() {
        if (crosshairView != null) return

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        crosshairParams = WindowManager.LayoutParams(
            240,
            240,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val customCrosshair = object : View(this) {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FF2A4D")
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }
            private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FF2A4D")
                style = Paint.Style.FILL
            }
            private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#22000000")
                strokeWidth = 5f
                style = Paint.Style.STROKE
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                val cx = width / 2f
                val cy = height / 2f
                val size = 26f
                val gap = 8f

                // Draw outline
                canvas.drawLine(cx - size, cy, cx - gap, cy, outlinePaint)
                canvas.drawLine(cx + gap, cy, cx + size, cy, outlinePaint)
                canvas.drawLine(cx, cy - size, cx, cy - gap, outlinePaint)
                canvas.drawLine(cx, cy + gap, cx, cy + size, outlinePaint)

                // Draw crosshair lines
                canvas.drawLine(cx - size, cy, cx - gap, cy, paint)
                canvas.drawLine(cx + gap, cy, cx + size, cy, paint)
                canvas.drawLine(cx, cy - size, cx, cy - gap, paint)
                canvas.drawLine(cx, cy + gap, cx, cy + size, paint)

                // Draw center dot
                canvas.drawCircle(cx, cy, 2.5f, dotPaint)
            }
        }

        crosshairView = customCrosshair
        try {
            windowManager?.addView(crosshairView, crosshairParams)
        } catch (_: Exception) {}
    }

    private fun createCyberHudBackground(): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.parseColor("#EE12141C"))
            cornerRadius = 16f
            setStroke(2, Color.parseColor("#55FF2A4D"))
        }
    }

    private fun startTelemetryLoop() {
        serviceScope.launch {
            while (isActive) {
                val statsTv = hudView?.findViewWithTag<TextView>("STATS_TV")
                if (statsTv != null && !isMinimized) {
                    val ram = DeviceInfoManager.getRamInfo(applicationContext)
                    val battery = DeviceInfoManager.getBatteryDetails(applicationContext)
                    val fps = PerformanceManager.liveMetrics.value.fps

                    val text = "FPS: $fps | RAM: ${ram.usedGb}G\nTEMP: ${battery.temperatureCelsius.toInt()}°C | BAT: ${battery.levelPercent}%"
                    statsTv.text = text
                }
                delay(1000)
            }
        }
    }

    private fun removeViews() {
        try {
            hudView?.let { windowManager?.removeView(it) }
            crosshairView?.let { windowManager?.removeView(it) }
        } catch (_: Exception) {}
        hudView = null
        crosshairView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        removeViews()
        PerformanceManager.stopFpsMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
