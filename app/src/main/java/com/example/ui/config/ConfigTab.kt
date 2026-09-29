package com.example.ui.config

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.ShizukuManager
import com.example.system.ShizukuStatus
import com.example.ui.theme.*

@Composable
fun ConfigTab(
    viewModel: ConfigViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ==================== SENSITIVITY LAB ====================
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            CyberSectionHeader(
                title = "SENSITIVITY LAB",
                badge = state.activePreset
            )

            // Legal & Calibration Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCardElevated)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Touch and display calibration only. Does not inject input or automate aiming.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Presets
            Text("CALIBRATION PRESETS", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Spacer(modifier = Modifier.height(6.dp))

            val presets = listOf("LOW", "BALANCED", "FAST")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = state.activePreset == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accentColor.copy(alpha = 0.2f) else CyberCardElevated)
                            .border(1.dp, if (isSelected) accentColor else CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.applyPreset(preset) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) accentColor else TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider: General Sensitivity
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("General Sensitivity", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.generalSens}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.generalSens.toFloat(),
                onValueChange = { viewModel.updateSensitivity(it.toInt(), state.touchResponse, state.swipeSpeed, state.pointerSpeed) },
                valueRange = 0f..100f
            )

            // Slider: Touch Response
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Touch Response", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.touchResponse}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.touchResponse.toFloat(),
                onValueChange = { viewModel.updateSensitivity(state.generalSens, it.toInt(), state.swipeSpeed, state.pointerSpeed) },
                valueRange = 0f..100f
            )

            // Slider: Swipe Speed
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Swipe Speed", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.swipeSpeed}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.swipeSpeed.toFloat(),
                onValueChange = { viewModel.updateSensitivity(state.generalSens, state.touchResponse, it.toInt(), state.pointerSpeed) },
                valueRange = 0f..100f
            )

            // Slider: Pointer Speed
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pointer Speed", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.pointerSpeed}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.pointerSpeed.toFloat(),
                onValueChange = { viewModel.updateSensitivity(state.generalSens, state.touchResponse, state.swipeSpeed, it.toInt()) },
                valueRange = 0f..100f
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Touch Hardware specs pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberCardElevated, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("DENSITY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${state.currentDensity} DPI", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberCardElevated, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("REFRESH", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${state.currentRefreshRate.toInt()} Hz", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberCardElevated, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("SAMPLING", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("240 Hz Max", style = MaterialTheme.typography.labelMedium, color = NeonCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==================== SCREEN SMOOTHER & REFRESH RATE ====================
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            CyberSectionHeader(title = "SCREEN SMOOTHER & REFRESH")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Interaction Smoother Level", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.screenSmootherLevel}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.screenSmootherLevel.toFloat(),
                onValueChange = { viewModel.updateScreenSmoother(it.toInt()) },
                valueRange = 20f..100f
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("TARGET REFRESH RATE", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.supportedRefreshRates) { rate ->
                    val rateInt = rate.toInt()
                    val isSelected = state.selectedRefreshRate == rateInt
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accentColor.copy(alpha = 0.2f) else CyberCardElevated)
                            .border(1.dp, if (isSelected) accentColor else CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setTargetRefreshRate(rateInt) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$rateInt Hz",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) accentColor else TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==================== RESOLUTION / DISPLAY LAB ====================
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            CyberSectionHeader(title = "RESOLUTION & DISPLAY LAB")

            // Current metrics display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberCardElevated, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CURRENT RESOLUTION", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${state.currentWidth} × ${state.currentHeight}", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("CURRENT DENSITY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${state.currentDensity} DPI", style = MaterialTheme.typography.titleMedium, color = accentColor)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inputs: Width, Height, Density
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.targetWidthInput,
                    onValueChange = { viewModel.onWidthInputChange(it) },
                    label = { Text("Width", fontSize = 11.sp, color = TextGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = state.targetHeightInput,
                    onValueChange = { viewModel.onHeightInputChange(it) },
                    label = { Text("Height", fontSize = 11.sp, color = TextGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = state.targetDensityInput,
                    onValueChange = { viewModel.onDensityInputChange(it) },
                    label = { Text("DPI", fontSize = 11.sp, color = TextGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.9f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shizuku Privilege Banner
            val (statusText, statusColor) = when (state.shizukuStatus) {
                ShizukuStatus.CONNECTED -> "SHIZUKU CONNECTED" to StatusSuccess
                ShizukuStatus.PERMISSION_REQUIRED -> "SHIZUKU PERMISSION REQUIRED" to StatusWarning
                ShizukuStatus.NOT_RUNNING -> "SHIZUKU SERVICE NOT RUNNING" to StatusWarning
                ShizukuStatus.UNSUPPORTED -> "SHIZUKU NOT INSTALLED" to TextMuted
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(statusText, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "CHECK",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    modifier = Modifier.clickable { viewModel.checkShizuku() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: APPLY, RESET
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CyberButton(
                    text = "APPLY RESOLUTION",
                    onClick = { viewModel.showConfirmDialog() },
                    icon = Icons.Default.AspectRatio,
                    modifier = Modifier.weight(1.2f)
                )
                CyberButton(
                    text = "RESET NATIVE",
                    onClick = { viewModel.resetResolutionAndDensity() },
                    icon = Icons.Default.Restore,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )
            }
        }
    }

    // Confirmation dialog before apply
    if (state.showConfirmApplyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmDialog() },
            containerColor = CyberCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusWarning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("APPLY DISPLAY RESOLUTION?", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    "You are requesting to modify display resolution to ${state.targetWidthInput} × ${state.targetHeightInput} (${state.targetDensityInput} DPI).\n\n" +
                    "If Shizuku is connected, the privileged command 'wm size' and 'wm density' will execute. You can always revert by pressing 'RESET NATIVE'.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.applyResolutionAndDensity() }) {
                    Text("CONFIRM APPLY", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDialog() }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }

    // Toast handler
    if (state.toastMessage != null) {
        LaunchedEffect(state.toastMessage) {
            android.widget.Toast.makeText(context, state.toastMessage, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
}
