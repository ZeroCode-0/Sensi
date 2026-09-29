package com.example.ui.shizuku

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.ShizukuStatus
import com.example.ui.theme.*

@Composable
fun ShizukuScreen(
    viewModel: ShizukuViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    val (badgeText, badgeColor) = when (state.status) {
        ShizukuStatus.CONNECTED -> "CONNECTED" to StatusSuccess
        ShizukuStatus.PERMISSION_REQUIRED -> "PERMISSION REQUIRED" to StatusWarning
        ShizukuStatus.NOT_RUNNING -> "NOT RUNNING" to StatusWarning
        ShizukuStatus.UNSUPPORTED -> "NOT INSTALLED" to TextMuted
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
                    text = "SHIZUKU PRIVILEGE CENTER",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ADB Privilege Interface",
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
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Status Hero Card
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = badgeColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("SHIZUKU STATUS", style = MaterialTheme.typography.labelSmall, color = TextGray)
                            Text(badgeText, style = MaterialTheme.typography.headlineSmall, color = badgeColor, fontWeight = FontWeight.Black)
                        }
                        CyberBadge(text = if (state.isInstalled) "INSTALLED" else "MISSING", color = if (state.isInstalled) StatusSuccess else TextMuted)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = when (state.status) {
                            ShizukuStatus.CONNECTED -> "Shizuku service is running with root/wireless ADB privileges. NEXA VORTEX can adjust resolution and system refresh rates directly."
                            ShizukuStatus.PERMISSION_REQUIRED -> "Shizuku is running on device, but NEXA VORTEX needs user permission approval."
                            ShizukuStatus.NOT_RUNNING -> "Shizuku app is installed but the background service has not been started via Wireless Debugging or ADB."
                            ShizukuStatus.UNSUPPORTED -> "Shizuku is not installed. Install the Shizuku app to enable elevated display and resolution controls without PC root."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = "CHECK STATUS",
                            onClick = { viewModel.checkStatus() },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Refresh,
                            isPrimary = false
                        )
                        if (state.status == ShizukuStatus.PERMISSION_REQUIRED) {
                            CyberButton(
                                text = "REQUEST PERMISSION",
                                onClick = { viewModel.requestPermission() },
                                modifier = Modifier.weight(1.2f),
                                icon = Icons.Default.Security
                            )
                        } else {
                            CyberButton(
                                text = "OPEN SHIZUKU",
                                onClick = { viewModel.openShizukuApp(context) },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.OpenInNew
                            )
                        }
                    }
                }
            }

            // Diagnostic Execution Card
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "SECURITY AUDIT & DIAGNOSTICS")

                    Text(
                        text = "Execute allowlisted privileged diagnostics to verify Shizuku IPC binder health.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = if (state.isRunningCommand) "RUNNING..." else "RUN DIAGNOSTICS",
                            onClick = { viewModel.runDiagnostics() },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Terminal,
                            enabled = !state.isRunningCommand && state.status == ShizukuStatus.CONNECTED
                        )
                        if (state.commandLog.isNotEmpty()) {
                            CyberButton(
                                text = "CLEAR",
                                onClick = { viewModel.clearLog() },
                                modifier = Modifier.width(90.dp),
                                isPrimary = false
                            )
                        }
                    }
                }
            }

            // Command Output Console
            if (state.commandLog.isNotEmpty()) {
                item {
                    Text("EXECUTION TERMINAL OUTPUT", style = MaterialTheme.typography.labelSmall, color = TextGray)
                }

                items(state.commandLog) { result ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF07080B))
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$ ${result.command}", style = MaterialTheme.typography.labelSmall, color = NeonCyan, fontFamily = FontFamily.Monospace)
                                Text("exit ${result.exitCode}", style = MaterialTheme.typography.labelSmall, color = if (result.isSuccess) StatusSuccess else StatusError)
                            }
                            if (result.output.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(result.output, style = MaterialTheme.typography.bodySmall, color = TextWhite, fontFamily = FontFamily.Monospace)
                            }
                            if (result.error.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(result.error, style = MaterialTheme.typography.bodySmall, color = StatusError, fontFamily = FontFamily.Monospace)
                            }
                        }
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
