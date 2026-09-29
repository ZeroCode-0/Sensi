package com.example.system

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

enum class ShizukuStatus {
    CONNECTED,
    PERMISSION_REQUIRED,
    NOT_RUNNING,
    UNSUPPORTED
}

data class ShizukuCommandResult(
    val command: String,
    val exitCode: Int,
    val output: String,
    val error: String,
    val isSuccess: Boolean
)

object ShizukuManager {

    private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    const val REQUEST_CODE_SHIZUKU_PERMISSION = 7001

    fun checkStatus(context: Context): ShizukuStatus {
        return try {
            if (!isShizukuInstalled(context)) {
                return ShizukuStatus.UNSUPPORTED
            }

            if (!Shizuku.pingBinder()) {
                return ShizukuStatus.NOT_RUNNING
            }

            if (Shizuku.isPreV11()) {
                return ShizukuStatus.UNSUPPORTED
            }

            val hasPermission = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                ShizukuStatus.CONNECTED
            } else {
                ShizukuStatus.PERMISSION_REQUIRED
            }
        } catch (_: Throwable) {
            ShizukuStatus.NOT_RUNNING
        }
    }

    fun isShizukuInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun requestPermission(listener: Shizuku.OnRequestPermissionResultListener) {
        try {
            if (Shizuku.isPreV11()) return
            Shizuku.addRequestPermissionResultListener(listener)
            Shizuku.requestPermission(REQUEST_CODE_SHIZUKU_PERMISSION)
        } catch (_: Throwable) {}
    }

    fun openShizukuApp(context: Context) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$SHIZUKU_PACKAGE")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(marketIntent)
            } catch (_: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
        }
    }

    suspend fun executeAllowlistedCommand(rawCommand: String): ShizukuCommandResult = withContext(Dispatchers.IO) {
        val cmd = rawCommand.trim()
        val isAllowed = cmd.startsWith("wm size") ||
                cmd.startsWith("wm density") ||
                cmd.startsWith("settings put system peak_refresh_rate") ||
                cmd.startsWith("settings put system min_refresh_rate") ||
                cmd.startsWith("getprop") ||
                cmd.startsWith("dumpsys display")

        if (!isAllowed) {
            return@withContext ShizukuCommandResult(
                command = cmd,
                exitCode = -1,
                output = "",
                error = "Security Policy: Command not in allowlist for security.",
                isSuccess = false
            )
        }

        if (!Shizuku.pingBinder()) {
            return@withContext ShizukuCommandResult(
                command = cmd,
                exitCode = -2,
                output = "",
                error = "Shizuku service is not running or binder is disconnected.",
                isSuccess = false
            )
        }

        return@withContext try {
            val method = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            method.isAccessible = true
            val process = method.invoke(null, arrayOf("sh", "-c", cmd), null, null) as Process

            val stdout = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            val stderr = BufferedReader(InputStreamReader(process.errorStream)).use { it.readText() }
            val exitCode = process.waitFor()

            ShizukuCommandResult(
                command = cmd,
                exitCode = exitCode,
                output = stdout.trim(),
                error = stderr.trim(),
                isSuccess = exitCode == 0
            )
        } catch (e: Throwable) {
            ShizukuCommandResult(
                command = cmd,
                exitCode = -3,
                output = "",
                error = e.localizedMessage ?: "Execution error",
                isSuccess = false
            )
        }
    }
}
