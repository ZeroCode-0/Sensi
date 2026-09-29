package com.example.ui.settings

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.ShizukuStatus
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

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
                    text = "VORTEX CONFIGURATION",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "System, Account & Aesthetics",
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
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. ACCOUNT SECTION
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "CYBER ACCOUNT", badge = "ACTIVE SESSION")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .border(1.dp, accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = accentColor)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = state.username.uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Administrator • Local Session Active",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }
                        }

                        CyberButton(
                            text = "LOGOUT",
                            onClick = { viewModel.logout(onLogout) },
                            icon = Icons.Default.Logout,
                            modifier = Modifier.height(36.dp),
                            isPrimary = false
                        )
                    }
                }
            }

            // 2. APPEARANCE SECTION
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "THEME & ACCENT COLOR")

                    Text(
                        text = "Customize the high-contrast cyber glow across the entire utility.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CyberAccent.entries.forEachIndexed { index, accent ->
                            val isSelected = state.accentIndex == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setAccentColor(index) }
                                    .padding(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(accent.color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else CyberBorder,
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = accent.displayName.split(" ").last(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) accent.color else TextGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 3. OVERLAY & PERFORMANCE PREFERENCES
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "OVERLAY & PERFORMANCE")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Keep Screen Awake", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                            Text("Prevent sleep timeout during Game Mode", style = MaterialTheme.typography.bodySmall, color = TextGray)
                        }
                        CyberSwitch(
                            checked = state.keepScreenAwake,
                            onCheckedChange = { viewModel.toggleKeepAwake(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Overlay Window Opacity", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                        Text("${(state.floatingOpacity * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
                    }
                    CyberSlider(
                        value = state.floatingOpacity,
                        onValueChange = { viewModel.setFloatingOpacity(it) },
                        valueRange = 0.3f..1.0f
                    )
                }
            }

            // 4. RESET SYSTEM
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "SYSTEM RESET MODULE")

                    Text(
                        text = "Reset all cached display resolution, crosshairs, sensitivity and game profiles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberButton(
                        text = "RESET ALL SETTINGS TO DEFAULTS",
                        onClick = { viewModel.promptResetAll() },
                        icon = Icons.Default.Restore,
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = false,
                        accentColor = StatusError
                    )
                }
            }

            // 5. ABOUT NEXA VORTEX
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    CyberSectionHeader(title = "ABOUT NEXA VORTEX")

                    Text(
                        text = "NEXA VORTEX • Version 1.0.0",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TAGLINE: CONTROL • OPTIMIZE • PLAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Engineered for Android high-performance gaming. Compliant with Google Play security policies and Android isolation boundaries. Supports Shizuku wireless ADB bridges for elevated display tuning.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
        }
    }

    // Reset confirmation dialog
    if (state.showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissResetDialog() },
            containerColor = CyberCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RESTORE FACTORY DEFAULTS?", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    "Are you sure you want to restore all NEXA VORTEX preferences and sensitivity calibration back to default settings?",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmResetAll() }) {
                    Text("YES, RESET ALL", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissResetDialog() }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }

    if (state.toastMessage != null) {
        LaunchedEffect(state.toastMessage) {
            android.widget.Toast.makeText(context, state.toastMessage, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
}
