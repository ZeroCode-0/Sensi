package com.example.ui.profiles

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.room.GameProfileEntity
import com.example.ui.theme.*

@Composable
fun GameProfilesScreen(
    viewModel: GameProfilesViewModel,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "GAME PROFILES",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Per-game optimization presets",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray
                    )
                }
            }

            CyberButton(
                text = "NEW PROFILE",
                onClick = { viewModel.openCreateDialog() },
                icon = Icons.Default.Add,
                modifier = Modifier.height(36.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            if (state.profiles.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No custom profiles yet. Create a new gaming profile above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                }
            }

            items(state.profiles) { profile ->
                val isActive = profile.isActivated
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isActive) StatusSuccess else CyberBorder,
                    backgroundColor = if (isActive) Color(0xFF101918) else CyberCard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.profileName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isActive) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CyberBadge(text = "ACTIVE", color = StatusSuccess)
                                }
                            }
                            Text(
                                text = "Game: ${profile.gameName} • ${profile.packageName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }

                        Row {
                            IconButton(onClick = { viewModel.duplicateProfile(profile) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextGray, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { viewModel.deleteProfile(profile) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Parameters Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).background(CyberCardElevated, RoundedCornerShape(6.dp)).padding(6.dp)) {
                            Column {
                                Text("REFRESH", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("${profile.refreshRatePreference} Hz", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(CyberCardElevated, RoundedCornerShape(6.dp)).padding(6.dp)) {
                            Column {
                                Text("BRIGHTNESS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("${profile.brightnessPercent}%", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(CyberCardElevated, RoundedCornerShape(6.dp)).padding(6.dp)) {
                            Column {
                                Text("CROSSHAIR", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(profile.crosshairPreset, style = MaterialTheme.typography.labelMedium, color = accentColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CyberButton(
                        text = if (isActive) "DEACTIVATE PROFILE" else "ACTIVATE PROFILE",
                        onClick = {
                            if (isActive) viewModel.deactivateAll() else viewModel.activateProfile(profile)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = !isActive,
                        accentColor = if (isActive) StatusWarning else StatusSuccess
                    )
                }
            }
        }
    }

    // Create Profile Dialog
    if (state.showCreateDialog) {
        CreateProfileDialog(
            installedApps = state.installedApps,
            onDismiss = { viewModel.closeCreateDialog() },
            onConfirm = { pName, gName, pkg, ref, bright, awake, floatM, ch ->
                viewModel.createProfile(pName, gName, pkg, ref, bright, awake, floatM, ch)
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

@Composable
private fun CreateProfileDialog(
    installedApps: List<InstalledApp>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, Int, Boolean, Boolean, String) -> Unit
) {
    val accentColor = LocalCyberAccent.current

    var profileName by remember { mutableStateOf("FPS Competitive") }
    var gameName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var refreshRate by remember { mutableIntStateOf(120) }
    var brightness by remember { mutableIntStateOf(85) }
    var keepAwake by remember { mutableStateOf(true) }
    var floatingMonitor by remember { mutableStateOf(true) }
    var crosshairPreset by remember { mutableStateOf("Classic") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberCardElevated,
        title = { Text("NEW GAME PROFILE", color = TextWhite, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("Profile Name", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    label = { Text("Game Name (e.g. Battle Royale)", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name (optional)", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Select from detected apps if available
                if (installedApps.isNotEmpty()) {
                    Text("Or choose detected app:", style = MaterialTheme.typography.labelSmall, color = TextGray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCard)
                            .padding(8.dp)
                    ) {
                        val firstApp = installedApps.first()
                        Text(
                            text = firstApp.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor,
                            modifier = Modifier.clickable {
                                gameName = firstApp.label
                                packageName = firstApp.packageName
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Keep Screen Awake", style = MaterialTheme.typography.bodySmall, color = TextWhite)
                    CyberSwitch(checked = keepAwake, onCheckedChange = { keepAwake = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Floating HUD Overlay", style = MaterialTheme.typography.bodySmall, color = TextWhite)
                    CyberSwitch(checked = floatingMonitor, onCheckedChange = { floatingMonitor = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(profileName, gameName, packageName, refreshRate, brightness, keepAwake, floatingMonitor, crosshairPreset)
            }) {
                Text("CREATE", color = accentColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextGray)
            }
        }
    )
}
