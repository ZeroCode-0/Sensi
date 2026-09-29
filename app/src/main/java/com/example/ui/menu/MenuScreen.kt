package com.example.ui.menu

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FloatingMonitorService
import com.example.ui.theme.*
import com.example.utils.PermissionUtils

@Composable
fun MenuScreen(
    onNavigateToShizuku: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToDeviceInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    var showOptimizerDialog by remember { mutableStateOf(false) }
    var showCacheDialog by remember { mutableStateOf(false) }
    var isFloatingHudActive by remember { mutableStateOf(false) }
    var showOverlayPermissionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
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
            Column {
                Text(
                    text = "VORTEX COMMAND SUITE",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Advanced Hardware & Privilege Modules",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Module 1: Shizuku Privilege Center
            item {
                MenuFeatureTile(
                    title = "SHIZUKU CENTER",
                    subtitle = "Manage privileged Wireless ADB / Root execution bridges",
                    badge = "PRIVILEGED",
                    icon = Icons.Default.Security,
                    badgeColor = StatusSuccess,
                    onClick = onNavigateToShizuku
                )
            }

            // Module 2: Floating Gaming HUD
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("FLOATING GAMING HUD", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                                Text("Real-time draggable in-game FPS/RAM/Temp widget", style = MaterialTheme.typography.bodySmall, color = TextGray)
                            }
                        }

                        CyberSwitch(
                            checked = isFloatingHudActive,
                            onCheckedChange = { active ->
                                if (active) {
                                    if (!PermissionUtils.canDrawOverlays(context)) {
                                        showOverlayPermissionDialog = true
                                    } else {
                                        isFloatingHudActive = true
                                        FloatingMonitorService.startHud(context)
                                    }
                                } else {
                                    isFloatingHudActive = false
                                    FloatingMonitorService.stop(context)
                                }
                            }
                        )
                    }
                }
            }

            // Module 3: Device Optimizer
            item {
                MenuFeatureTile(
                    title = "DEVICE OPTIMIZER",
                    subtitle = "Audits Display, Battery, Memory, Network & System states",
                    badge = "DIAGNOSTICS",
                    icon = Icons.Default.Tune,
                    badgeColor = NeonCyan,
                    onClick = { showOptimizerDialog = true }
                )
            }

            // Module 4: Game Profiles Manager
            item {
                MenuFeatureTile(
                    title = "GAME PROFILES",
                    subtitle = "Custom refresh rates, brightness and crosshair presets per game",
                    badge = "PROFILES",
                    icon = Icons.Default.SportsEsports,
                    badgeColor = NeonPurple,
                    onClick = onNavigateToProfiles
                )
            }

            // Module 5: Network Latency & Speed Monitor
            item {
                MenuFeatureTile(
                    title = "NETWORK MONITOR",
                    subtitle = "Run live TCP/ICMP ping tests to Google/Cloudflare gaming servers",
                    badge = "SPEED TEST",
                    icon = Icons.Default.Wifi,
                    badgeColor = NeonGreen,
                    onClick = onNavigateToNetwork
                )
            }

            // Module 6: Raw Device Information
            item {
                MenuFeatureTile(
                    title = "DEVICE INFORMATION",
                    subtitle = "Detailed hardware specs, CPU cores, RAM, and display metrics",
                    badge = "HARDWARE",
                    icon = Icons.Default.Info,
                    badgeColor = TextWhite,
                    onClick = onNavigateToDeviceInfo
                )
            }

            // Module 7: Cache & Storage Manager
            item {
                MenuFeatureTile(
                    title = "CACHE & STORAGE INFO",
                    subtitle = "Transparent Android storage sandbox overview & system cleaner",
                    badge = "STORAGE",
                    icon = Icons.Default.CleaningServices,
                    badgeColor = StatusWarning,
                    onClick = { showCacheDialog = true }
                )
            }
        }
    }

    // Device Optimizer Dialog
    if (showOptimizerDialog) {
        DeviceOptimizerDialog(onDismiss = { showOptimizerDialog = false })
    }

    // Cache / Storage Dialog
    if (showCacheDialog) {
        AlertDialog(
            onDismissRequest = { showCacheDialog = false },
            containerColor = CyberCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = StatusWarning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("STORAGE & CACHE POLICY", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    "On modern Android (API 26+), third-party applications are strictly prevented by system security sandboxes from deleting the private cache of other apps.\n\n" +
                    "NEXA VORTEX does not provide fake 'clear 2GB RAM/Cache' mock buttons. Instead, you can launch Android's native Internal Storage cleaner directly.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showCacheDialog = false
                    try {
                        val intent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        PermissionUtils.openAppDetailsSettings(context)
                    }
                }) {
                    Text("OPEN STORAGE SETTINGS", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCacheDialog = false }) {
                    Text("CLOSE", color = TextGray)
                }
            }
        )
    }

    // Overlay Permission Request Dialog
    if (showOverlayPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showOverlayPermissionDialog = false },
            containerColor = CyberCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PERMISSION REQUIRED", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    "To show the floating real-time gaming HUD over games, please enable the 'Display over other apps' permission in Android Settings.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showOverlayPermissionDialog = false
                    PermissionUtils.requestOverlayPermission(context)
                }) {
                    Text("GRANT PERMISSION", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOverlayPermissionDialog = false }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun MenuFeatureTile(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    badgeColor: Color,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextGray)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}

@Composable
private fun DeviceOptimizerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberCardElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("OPTIMIZER MATRIX", color = TextWhite, style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OptimizerCategoryItem("DISPLAY", "Refresh rate modes & smooth animation", "AVAILABLE") {
                    context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                }
                OptimizerCategoryItem("BATTERY", "Power profile & Battery Saver tuning", "AVAILABLE") {
                    context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                }
                OptimizerCategoryItem("MEMORY", "Real-time RAM tracking & usage audit", "MONITORED") {
                    // Handled inside app
                }
                OptimizerCategoryItem("NETWORK", "Wireless & cellular throughput optimization", "AVAILABLE") {
                    context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                }
                OptimizerCategoryItem("SYSTEM", "Privileged ADB Shizuku bridge access", "ALLOWLISTED") {
                    // Shizuku
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = accentColor, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun OptimizerCategoryItem(
    title: String,
    description: String,
    status: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberCard, RoundedCornerShape(8.dp))
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .clickable { onAction() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextGray)
            }
            CyberBadge(text = status, color = NeonCyan)
        }
    }
}
