package com.example.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.config.ConfigTab
import com.example.ui.config.ConfigViewModel
import com.example.ui.crosshair.CrosshairTab
import com.example.ui.crosshair.CrosshairViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    crosshairViewModel: CrosshairViewModel,
    configViewModel: ConfigViewModel,
    modifier: Modifier = Modifier
) {
    val state by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    val tabs = listOf("MAIN MENU", "CROSSHAIR", "CONFIG")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (state.isGameModeActive) StatusSuccess else accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEXA VORTEX",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = if (state.isGameModeActive) "GAME MODE RUNNING" else "SYSTEM IDLE • READY",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.isGameModeActive) StatusSuccess else TextGray,
                    letterSpacing = 0.8.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { homeViewModel.refreshStats() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberCardElevated, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Top Level Tabs: MAIN MENU, CROSSHAIR, CONFIG
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyberSurface)
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(3.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = state.selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) accentColor else Color.Transparent)
                        .clickable { homeViewModel.selectTab(index) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.Black else TextGray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (state.selectedTab) {
                0 -> MainMenuDashboard(homeViewModel = homeViewModel)
                1 -> CrosshairTab(viewModel = crosshairViewModel)
                2 -> ConfigTab(viewModel = configViewModel)
            }
        }
    }

    // Toast handler
    if (state.toastMessage != null) {
        LaunchedEffect(state.toastMessage) {
            android.widget.Toast.makeText(context, state.toastMessage, android.widget.Toast.LENGTH_SHORT).show()
            homeViewModel.clearToast()
        }
    }
}

@Composable
private fun MainMenuDashboard(
    homeViewModel: HomeViewModel
) {
    val state by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        // Hero: GAME MODE CONTROL CARD
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (state.isGameModeActive) StatusSuccess else accentColor,
                backgroundColor = if (state.isGameModeActive) Color(0xFF101918) else CyberCard
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = if (state.isGameModeActive) StatusSuccess else accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VORTEX GAME MODE",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = if (state.isGameModeActive) "High-priority service active • WakeLock held" else "Tap to prioritize gaming telemetry & display awake",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }

                    CyberBadge(
                        text = if (state.isGameModeActive) "ENGAGED" else "STANDBY",
                        color = if (state.isGameModeActive) StatusSuccess else accentColor
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Keep Screen Awake",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite
                    )
                    CyberSwitch(
                        checked = state.keepAwake,
                        onCheckedChange = { homeViewModel.toggleKeepAwake(it) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CyberButton(
                    text = if (state.isGameModeActive) "STOP GAME MODE" else "START GAME MODE",
                    onClick = { homeViewModel.toggleGameMode(context) },
                    icon = if (state.isGameModeActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = if (state.isGameModeActive) StatusWarning else accentColor
                )
            }
        }

        // Section: LIVE HARDWARE TELEMETRY
        item {
            CyberSectionHeader(title = "HARDWARE PERFORMANCE MATRIX", badge = "LIVE SENSORS")
        }

        // Cards 1 & 2: Device Performance & RAM Usage
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 1: Device Performance
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "PERFORMANCE",
                    value = if (state.isGameModeActive) "OPTIMIZED" else "BALANCED",
                    subtext = "Power governor active",
                    icon = Icons.Default.Speed,
                    accentColor = if (state.isGameModeActive) StatusSuccess else accentColor
                )

                // Card 2: RAM Usage
                CyberCard(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("RAM USAGE", style = MaterialTheme.typography.labelSmall, color = TextGray)
                        Icon(Icons.Default.Memory, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${state.ramInfo.usedGb} / ${state.ramInfo.totalGb} GB",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.ramInfo.usedPercent}% Allocated",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CyberProgressBar(progress = state.ramInfo.usedPercent / 100f)
                }
            }
        }

        // Cards 3 & 4: CPU Usage & Battery
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 3: CPU Usage
                CyberCard(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("CPU USAGE", style = MaterialTheme.typography.labelSmall, color = TextGray)
                        Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${state.cpuPercent}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "8 Cores Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CyberProgressBar(progress = state.cpuPercent / 100f, color = NeonCyan)
                }

                // Card 4: Battery
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "BATTERY",
                    value = "${state.batteryDetails.levelPercent}%",
                    subtext = if (state.batteryDetails.isCharging) "Charging • ${state.batteryDetails.health}" else "Discharging • ${state.batteryDetails.health}",
                    icon = if (state.batteryDetails.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                    accentColor = if (state.batteryDetails.levelPercent > 20) StatusSuccess else StatusWarning
                )
            }
        }

        // Cards 5 & 6: Temperature & FPS Monitor
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 5: Temperature
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "TEMPERATURE",
                    value = "${state.batteryDetails.temperatureCelsius.toInt()}°C",
                    subtext = when {
                        state.batteryDetails.temperatureCelsius < 38f -> "Normal Thermal"
                        state.batteryDetails.temperatureCelsius < 43f -> "Warm Game Load"
                        else -> "Thermal Throttling Risk"
                    },
                    icon = Icons.Default.Thermostat,
                    accentColor = if (state.batteryDetails.temperatureCelsius > 42f) StatusError else accentColor
                )

                // Card 6: FPS Monitor
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "FPS MONITOR",
                    value = "${state.liveFps} FPS",
                    subtext = "Sync: ${state.displayDetails.currentRefreshRate.toInt()} Hz",
                    icon = Icons.Default.MonitorHeart,
                    accentColor = NeonGreen
                )
            }
        }

        // Cards 7 & 8: Current Resolution & Screen Refresh Rate
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 7: Current Resolution
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "RESOLUTION",
                    value = "${state.displayDetails.width} × ${state.displayDetails.height}",
                    subtext = "${state.displayDetails.densityDpi} DPI Scaled",
                    icon = Icons.Default.FitScreen,
                    accentColor = NeonCyan
                )

                // Card 8: Refresh Rate
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "REFRESH RATE",
                    value = "${state.displayDetails.currentRefreshRate.toInt()} Hz",
                    subtext = "Modes: ${state.displayDetails.supportedRefreshRates.joinToString { it.toInt().toString() }}",
                    icon = Icons.Default.Autorenew,
                    accentColor = NeonPurple
                )
            }
        }

        // Cards 9 & 10: Storage & Network Status
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 9: Storage
                CyberCard(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("STORAGE", style = MaterialTheme.typography.labelSmall, color = TextGray)
                        Icon(Icons.Default.Storage, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${state.storageInfo.usedGb} / ${state.storageInfo.totalGb} GB",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.storageInfo.usedPercent}% Used",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CyberProgressBar(progress = state.storageInfo.usedPercent / 100f)
                }

                // Card 10: Network Status
                CyberTelemetryCard(
                    modifier = Modifier.weight(1f),
                    title = "NETWORK",
                    value = state.networkState.connectionType,
                    subtext = if (state.networkState.isConnected) "Link: ${state.networkState.linkDownSpeedMbps} Mbps" else "Offline",
                    icon = Icons.Default.Wifi,
                    accentColor = if (state.networkState.isConnected) StatusSuccess else StatusError
                )
            }
        }
    }
}

@Composable
fun CyberTelemetryCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalCyberAccent.current
) {
    CyberCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextGray)
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            fontWeight = FontWeight.Black
        )
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}
