package com.example.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

data class NetworkInfoState(
    val connectionType: String = "Detecting...",
    val isConnected: Boolean = false,
    val linkDownSpeedMbps: Int = 0,
    val linkUpSpeedMbps: Int = 0,
    val pingMs: Long = -1,
    val ipAddress: String = "N/A"
)

object NetworkManager {

    fun getNetworkState(context: Context): NetworkInfoState {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return NetworkInfoState("Offline", false)
        val caps = cm.getNetworkCapabilities(network) ?: return NetworkInfoState("Offline", false)

        val isConnected = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val connType = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi 802.11"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 4G/5G"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Active Network"
        }

        val downSpeed = caps.linkDownstreamBandwidthKbps / 1000
        val upSpeed = caps.linkUpstreamBandwidthKbps / 1000

        var ip = "N/A"
        try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ipInt = wm?.connectionInfo?.ipAddress ?: 0
            if (ipInt != 0) {
                ip = String.format(
                    "%d.%d.%d.%d",
                    ipInt and 0xff,
                    ipInt shr 8 and 0xff,
                    ipInt shr 16 and 0xff,
                    ipInt shr 24 and 0xff
                )
            }
        } catch (_: Exception) {}

        return NetworkInfoState(
            connectionType = connType,
            isConnected = isConnected,
            linkDownSpeedMbps = downSpeed.coerceAtLeast(1),
            linkUpSpeedMbps = upSpeed.coerceAtLeast(1),
            ipAddress = ip
        )
    }

    suspend fun runPingTest(host: String = "8.8.8.8", port: Int = 53, timeoutMs: Int = 2000): Long = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMs)
            }
            val elapsed = System.currentTimeMillis() - startTime
            elapsed
        } catch (_: Exception) {
            -1L
        }
    }

    suspend fun sampleThroughputKbps(durationMs: Long = 1000): Long = withContext(Dispatchers.IO) {
        val startBytes = TrafficStats.getTotalRxBytes()
        delay(durationMs)
        val endBytes = TrafficStats.getTotalRxBytes()
        if (startBytes == TrafficStats.UNSUPPORTED.toLong() || endBytes == TrafficStats.UNSUPPORTED.toLong()) {
            return@withContext 0L
        }
        val diffBytes = endBytes - startBytes
        val kbps = (diffBytes * 8) / (durationMs / 1000.0) / 1000.0
        kbps.toLong().coerceAtLeast(0)
    }
}
