package com.example.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.system.DeviceInfoManager
import com.example.system.PerformanceManager
import kotlinx.coroutines.*

class PerformanceService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID = "nexa_performance_channel"
        const val NOTIFICATION_ID = 8801
        const val ACTION_START = "ACTION_START_PERFORMANCE_SERVICE"
        const val ACTION_STOP = "ACTION_STOP_PERFORMANCE_SERVICE"

        fun startService(context: Context) {
            val intent = Intent(context, PerformanceService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, PerformanceService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        PerformanceManager.startFpsMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification("Optimizing performance & telemetry..."))
        acquireWakeLock()
        startTelemetryLoop()

        return START_STICKY
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, "NexaVortex:GameModeWakeLock")
            wakeLock?.acquire(3 * 60 * 60 * 1000L) // 3 hours max
        } catch (_: Exception) {}
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {}
    }

    private fun startTelemetryLoop() {
        serviceScope.launch {
            while (isActive) {
                val ram = DeviceInfoManager.getRamInfo(applicationContext)
                val battery = DeviceInfoManager.getBatteryDetails(applicationContext)
                val cpu = PerformanceManager.readCpuUsage()
                val display = DeviceInfoManager.getDisplayDetails(applicationContext)

                PerformanceManager.updateMetrics(
                    ramUsedGb = ram.usedGb,
                    ramTotalGb = ram.totalGb,
                    ramPercent = ram.usedPercent,
                    cpuPercent = cpu,
                    batteryPercent = battery.levelPercent,
                    tempCelsius = battery.temperatureCelsius,
                    refreshRate = display.currentRefreshRate.toInt(),
                    isGameModeActive = true
                )

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val text = "FPS: ${PerformanceManager.liveMetrics.value.fps} | RAM: ${ram.usedGb}GB / ${ram.totalGb}GB | Temp: ${battery.temperatureCelsius.toInt()}°C"
                notificationManager.notify(NOTIFICATION_ID, buildNotification(text))

                delay(1500)
            }
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, PerformanceService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NEXA VORTEX • GAME MODE ACTIVE")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop Game Mode", stopPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Game Mode & Telemetry",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Live game performance status and telemetry service"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        releaseWakeLock()
        PerformanceManager.stopFpsMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
