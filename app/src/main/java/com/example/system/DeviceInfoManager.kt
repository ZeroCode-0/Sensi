package com.example.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.view.Display
import android.view.WindowManager
import kotlin.math.roundToInt

data class RamInfo(
    val totalGb: Double,
    val availableGb: Double,
    val usedGb: Double,
    val usedPercent: Int
)

data class StorageInfo(
    val totalGb: Double,
    val freeGb: Double,
    val usedGb: Double,
    val usedPercent: Int
)

data class BatteryDetails(
    val levelPercent: Int,
    val isCharging: Boolean,
    val temperatureCelsius: Float,
    val voltageMv: Int,
    val health: String,
    val technology: String
)

data class DisplayDetails(
    val width: Int,
    val height: Int,
    val densityDpi: Int,
    val currentRefreshRate: Float,
    val supportedRefreshRates: List<Float>
)

data class DeviceSpec(
    val brand: String,
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val kernelVersion: String,
    val cpuAbi: String,
    val cpuCores: Int
)

object DeviceInfoManager {

    fun getDeviceSpec(): DeviceSpec {
        return DeviceSpec(
            brand = Build.BRAND.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A",
            kernelVersion = System.getProperty("os.version") ?: "Linux",
            cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a",
            cpuCores = Runtime.getRuntime().availableProcessors()
        )
    }

    fun getRamInfo(context: Context): RamInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        val availGb = memoryInfo.availMem / (1024.0 * 1024.0 * 1024.0)
        val usedGb = totalGb - availGb
        val percent = if (totalGb > 0) ((usedGb / totalGb) * 100).roundToInt() else 0

        return RamInfo(
            totalGb = (totalGb * 10.0).roundToInt() / 10.0,
            availableGb = (availGb * 10.0).roundToInt() / 10.0,
            usedGb = (usedGb * 10.0).roundToInt() / 10.0,
            usedPercent = percent.coerceIn(0, 100)
        )
    }

    fun getStorageInfo(): StorageInfo {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            val usedBytes = totalBytes - freeBytes

            val totalGb = totalBytes / (1024.0 * 1024.0 * 1024.0)
            val freeGb = freeBytes / (1024.0 * 1024.0 * 1024.0)
            val usedGb = usedBytes / (1024.0 * 1024.0 * 1024.0)
            val percent = if (totalGb > 0) ((usedGb / totalGb) * 100).roundToInt() else 0

            StorageInfo(
                totalGb = (totalGb * 10.0).roundToInt() / 10.0,
                freeGb = (freeGb * 10.0).roundToInt() / 10.0,
                usedGb = (usedGb * 10.0).roundToInt() / 10.0,
                usedPercent = percent.coerceIn(0, 100)
            )
        } catch (_: Exception) {
            StorageInfo(64.0, 32.0, 32.0, 50)
        }
    }

    fun getBatteryDetails(context: Context): BatteryDetails {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 50
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (scale > 0 && level >= 0) (level * 100 / scale) else 50

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempCelsius = if (temp > 0) temp / 10.0f else 35.0f

        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 4000
        val technology = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        val healthCode = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Normal"
        }

        return BatteryDetails(
            levelPercent = batteryPct,
            isCharging = isCharging,
            temperatureCelsius = tempCelsius,
            voltageMv = voltage,
            health = health,
            technology = technology
        )
    }

    fun getDisplayDetails(context: Context): DisplayDetails {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            displayManager.getDisplay(Display.DEFAULT_DISPLAY) ?: windowManager.defaultDisplay
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        val metrics = context.resources.displayMetrics
        val width: Int
        val height: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            width = bounds.width()
            height = bounds.height()
        } else {
            width = metrics.widthPixels
            height = metrics.heightPixels
        }

        val currentRate = display?.refreshRate ?: 60.0f
        val supportedRates = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && display != null) {
            display.supportedModes.map { it.refreshRate.roundToInt().toFloat() }.distinct().sorted()
        } else {
            listOf(60.0f)
        }

        return DisplayDetails(
            width = width,
            height = height,
            densityDpi = metrics.densityDpi,
            currentRefreshRate = currentRate,
            supportedRefreshRates = if (supportedRates.isNotEmpty()) supportedRates else listOf(60f)
        )
    }
}
