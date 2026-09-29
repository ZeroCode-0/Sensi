package com.example.ui.network

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

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
                    text = "NETWORK LATENCY & SPEEDS",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Safe TCP / ICMP Connectivity Monitor",
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
            // Hero Status Card
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (state.info.isConnected) StatusSuccess else StatusError
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("CONNECTION STATUS", style = MaterialTheme.typography.labelSmall, color = TextGray)
                            Text(
                                text = state.info.connectionType,
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextWhite,
                                fontWeight = FontWeight.Black
                            )
                        }
                        CyberBadge(
                            text = if (state.info.isConnected) "ONLINE" else "OFFLINE",
                            color = if (state.info.isConnected) StatusSuccess else StatusError
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).background(CyberCardElevated, RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column {
                                Text("DOWNLINK SPEED", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("${state.info.linkDownSpeedMbps} Mbps", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(CyberCardElevated, RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column {
                                Text("UPLINK SPEED", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("${state.info.linkUpSpeedMbps} Mbps", style = MaterialTheme.typography.titleMedium, color = NeonGreen)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(CyberCardElevated, RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column {
                                Text("IP ADDRESS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(state.info.ipAddress, style = MaterialTheme.typography.titleMedium, color = TextWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Real Ping Latency Tester
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "PACKET LATENCY TEST")

                    Text(
                        text = "Measures round-trip packet latency to safe public DNS gaming nodes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Server Selection
                    val servers = listOf("8.8.8.8 (Google DNS)", "1.1.1.1 (Cloudflare)")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        servers.forEach { s ->
                            val isSel = state.selectedServer == s
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) accentColor.copy(alpha = 0.2f) else CyberCardElevated)
                                    .border(1.dp, if (isSel) accentColor else CyberBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setServer(s) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = s,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) accentColor else TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ping Result Readout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCardElevated, RoundedCornerShape(10.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PING LATENCY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = if (state.lastPingMs > 0) "${state.lastPingMs} ms" else "-- ms",
                                style = MaterialTheme.typography.headlineLarge,
                                color = when {
                                    state.lastPingMs <= 0 -> TextMuted
                                    state.lastPingMs < 50 -> StatusSuccess
                                    state.lastPingMs < 100 -> StatusWarning
                                    else -> StatusError
                                },
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = when {
                                    state.lastPingMs <= 0 -> "Ready for test"
                                    state.lastPingMs < 50 -> "Excellent esports tier"
                                    state.lastPingMs < 100 -> "Moderate latency"
                                    else -> "High jitter / packet delay"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = if (state.isPinging) "TESTING..." else "RUN PING TEST",
                            onClick = { viewModel.runPingTest() },
                            modifier = Modifier.weight(1.2f),
                            icon = Icons.Default.NetworkCheck,
                            enabled = !state.isPinging
                        )

                        CyberButton(
                            text = "NETWORK SETTINGS",
                            onClick = {
                                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Settings,
                            isPrimary = false
                        )
                    }
                }
            }
        }
    }

    if (state.toastMessage != null) {
        LaunchedEffect(state.toastMessage) {
            android.widget.Toast.makeText(context, state.toastMessage, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
}
