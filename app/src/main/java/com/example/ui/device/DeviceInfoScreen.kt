package com.example.ui.device

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.DeviceInfoManager
import com.example.ui.theme.*

@Composable
fun DeviceInfoScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    val spec = remember { DeviceInfoManager.getDeviceSpec() }
    val ram = remember { DeviceInfoManager.getRamInfo(context) }
    val storage = remember { DeviceInfoManager.getStorageInfo() }
    val battery = remember { DeviceInfoManager.getBatteryDetails(context) }
    val display = remember { DeviceInfoManager.getDisplayDetails(context) }

    fun copyToClipboard(label: String, value: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .background(CyberCardElevated, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "DEVICE TELEMETRY & HARDWARE",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Raw System Parameters",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // General Info Card
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "CORE SYSTEM IDENTIFIERS", badge = spec.brand)

                    InfoRow("Device Model", "${spec.manufacturer} ${spec.model}") { copyToClipboard("Model", "${spec.manufacturer} ${spec.model}") }
                    InfoRow("Android Version", "Android ${spec.androidVersion} (API ${spec.apiLevel})") { copyToClipboard("Android Version", spec.androidVersion) }
                    InfoRow("Security Patch", spec.securityPatch) { copyToClipboard("Patch", spec.securityPatch) }
                    InfoRow("Kernel Version", spec.kernelVersion) { copyToClipboard("Kernel", spec.kernelVersion) }
                    InfoRow("Primary CPU ABI", spec.cpuAbi) { copyToClipboard("ABI", spec.cpuAbi) }
                    InfoRow("Active CPU Cores", "${spec.cpuCores} Cores Available") { copyToClipboard("Cores", spec.cpuCores.toString()) }
                }
            }

            // Display & GPU Specs
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "DISPLAY & VISUAL SUBSYSTEM", badge = "${display.currentRefreshRate.toInt()}Hz")

                    InfoRow("Native Resolution", "${display.width} × ${display.height} px") { copyToClipboard("Resolution", "${display.width}x${display.height}") }
                    InfoRow("Display Density", "${display.densityDpi} DPI") { copyToClipboard("DPI", display.densityDpi.toString()) }
                    InfoRow("Active Refresh Rate", "${display.currentRefreshRate.toInt()} Hz") { copyToClipboard("Refresh", display.currentRefreshRate.toString()) }
                    InfoRow("Supported Modes", display.supportedRefreshRates.joinToString(", ") { "${it.toInt()}Hz" }) { copyToClipboard("Modes", display.supportedRefreshRates.toString()) }
                }
            }

            // Memory & Storage Specs
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "MEMORY & STORAGE ALLOCATION")

                    InfoRow("Total Physical RAM", "${ram.totalGb} GB") { copyToClipboard("Total RAM", ram.totalGb.toString()) }
                    InfoRow("Available Free RAM", "${ram.availableGb} GB (${100 - ram.usedPercent}% free)") { copyToClipboard("Free RAM", ram.availableGb.toString()) }
                    InfoRow("Used RAM", "${ram.usedGb} GB (${ram.usedPercent}%)") { copyToClipboard("Used RAM", ram.usedGb.toString()) }
                    InfoRow("Internal Storage Total", "${storage.totalGb} GB") { copyToClipboard("Total Storage", storage.totalGb.toString()) }
                    InfoRow("Internal Storage Free", "${storage.freeGb} GB") { copyToClipboard("Free Storage", storage.freeGb.toString()) }
                }
            }

            // Battery Subsystem
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "POWER & THERMAL SUBSYSTEM", badge = "${battery.temperatureCelsius.toInt()}°C")

                    InfoRow("Battery Level", "${battery.levelPercent}%") { copyToClipboard("Battery", "${battery.levelPercent}%") }
                    InfoRow("Charging State", if (battery.isCharging) "Connected / Charging" else "Discharging") { copyToClipboard("Charging", battery.isCharging.toString()) }
                    InfoRow("Battery Temperature", "${battery.temperatureCelsius}°C") { copyToClipboard("Battery Temp", "${battery.temperatureCelsius}°C") }
                    InfoRow("Battery Voltage", "${battery.voltageMv} mV") { copyToClipboard("Voltage", "${battery.voltageMv} mV") }
                    InfoRow("Battery Health", battery.health) { copyToClipboard("Health", battery.health) }
                    InfoRow("Battery Technology", battery.technology) { copyToClipboard("Technology", battery.technology) }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextWhite, fontWeight = FontWeight.SemiBold)
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextGray, modifier = Modifier.size(14.dp))
        }
    }
}
