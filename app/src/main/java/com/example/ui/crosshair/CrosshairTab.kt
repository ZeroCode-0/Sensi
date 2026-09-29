package com.example.ui.crosshair

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.room.CrosshairPresetEntity
import com.example.ui.theme.*
import com.example.utils.PermissionUtils

@Composable
fun CrosshairTab(
    viewModel: CrosshairViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val accentColor = LocalCyberAccent.current

    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }

    val presetColors = listOf(
        "#FF2A4D" to "Crimson",
        "#00FF88" to "Acid Green",
        "#00E5FF" to "Cyan",
        "#FFD600" to "Yellow",
        "#FFFFFF" to "White",
        "#B026FF" to "Neon Purple"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Legal & Fair Play Notice Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberCardElevated.copy(alpha = 0.5f))
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = StatusSuccess,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Visual overlay utility only. No memory injection or recoil automation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Preview & Directional Controls Grid
        CyberCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = accentColor.copy(alpha = 0.4f)
        ) {
            CyberSectionHeader(title = "CROSSHAIR REAL-TIME PREVIEW", badge = state.shape)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Directional controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ALIGNMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // UP
                    IconButton(
                        onClick = { viewModel.adjustOffset(0f, -2f) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(CyberCardElevated, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = accentColor)
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // LEFT
                        IconButton(
                            onClick = { viewModel.adjustOffset(-2f, 0f) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyberCardElevated, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = accentColor)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // CENTER RESET
                        IconButton(
                            onClick = { viewModel.resetOffset() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyberCardElevated, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.CenterFocusStrong, contentDescription = "Center", tint = TextWhite)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // RIGHT
                        IconButton(
                            onClick = { viewModel.adjustOffset(2f, 0f) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyberCardElevated, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = accentColor)
                        }
                    }

                    // DOWN
                    IconButton(
                        onClick = { viewModel.adjustOffset(0f, 2f) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(CyberCardElevated, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = accentColor)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Offset: (${state.offsetX.toInt()}, ${state.offsetY.toInt()})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                // Right side: Canvas Live Preview Panel
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF07080B))
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val crosshairColor = try {
                        Color(android.graphics.Color.parseColor(state.colorHex)).copy(alpha = state.opacity)
                    } catch (_: Exception) {
                        NeonCrimson.copy(alpha = state.opacity)
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f + state.offsetX
                        val cy = size.height / 2f + state.offsetY

                        rotate(state.rotation, pivot = Offset(cx, cy)) {
                            val strokeWidth = state.thickness * density
                            val halfSize = state.size * density
                            val gap = state.gap * density

                            // Draw outline shadow if enabled
                            if (state.outlineEnabled) {
                                val outlineWidth = strokeWidth + 2.5f * density
                                val outlineColor = Color.Black.copy(alpha = 0.8f)

                                // Cross lines outline
                                drawLine(outlineColor, Offset(cx - halfSize, cy), Offset(cx - gap, cy), outlineWidth, StrokeCap.Square)
                                drawLine(outlineColor, Offset(cx + gap, cy), Offset(cx + halfSize, cy), outlineWidth, StrokeCap.Square)
                                drawLine(outlineColor, Offset(cx, cy - halfSize), Offset(cx, cy - gap), outlineWidth, StrokeCap.Square)
                                drawLine(outlineColor, Offset(cx, cy + gap), Offset(cx, cy + halfSize), outlineWidth, StrokeCap.Square)

                                if (state.shape == "Circle") {
                                    drawCircle(outlineColor, radius = halfSize, center = Offset(cx, cy), style = Stroke(width = outlineWidth))
                                }
                            }

                            // Main Cross lines
                            drawLine(crosshairColor, Offset(cx - halfSize, cy), Offset(cx - gap, cy), strokeWidth, StrokeCap.Square)
                            drawLine(crosshairColor, Offset(cx + gap, cy), Offset(cx + halfSize, cy), strokeWidth, StrokeCap.Square)
                            drawLine(crosshairColor, Offset(cx, cy - halfSize), Offset(cx, cy - gap), strokeWidth, StrokeCap.Square)
                            drawLine(crosshairColor, Offset(cx, cy + gap), Offset(cx, cy + halfSize), strokeWidth, StrokeCap.Square)

                            if (state.shape == "Circle") {
                                drawCircle(crosshairColor, radius = halfSize, center = Offset(cx, cy), style = Stroke(width = strokeWidth))
                            }

                            // Center dot
                            if (state.dotEnabled) {
                                if (state.outlineEnabled) {
                                    drawCircle(Color.Black.copy(alpha = 0.8f), radius = strokeWidth + 1f, center = Offset(cx, cy))
                                }
                                drawCircle(crosshairColor, radius = strokeWidth.coerceAtLeast(2f), center = Offset(cx, cy))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Preset selector chips
            Text(
                text = "SHAPE PRESETS",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.presets) { preset ->
                    val isSelected = state.shape == preset.shape && state.size == preset.size
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accentColor.copy(alpha = 0.2f) else CyberCardElevated)
                            .border(1.dp, if (isSelected) accentColor else CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.applyPreset(preset) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) accentColor else TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sliders & Customization Controls
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            CyberSectionHeader(title = "PRECISION ADJUSTMENTS")

            // Size slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Size", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.size.toInt()} px", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.size,
                onValueChange = { viewModel.updateSize(it) },
                valueRange = 10f..60f
            )

            // Thickness slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Thickness", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.thickness.toInt()} px", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.thickness,
                onValueChange = { viewModel.updateThickness(it) },
                valueRange = 1f..10f
            )

            // Gap slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Center Gap", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.gap.toInt()} px", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.gap,
                onValueChange = { viewModel.updateGap(it) },
                valueRange = 0f..30f
            )

            // Opacity slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Opacity", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${(state.opacity * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.opacity,
                onValueChange = { viewModel.updateOpacity(it) },
                valueRange = 0.2f..1.0f
            )

            // Rotation slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rotation Angle", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Text("${state.rotation.toInt()}°", style = MaterialTheme.typography.labelMedium, color = accentColor)
            }
            CyberSlider(
                value = state.rotation,
                onValueChange = { viewModel.updateRotation(it) },
                valueRange = 0f..90f
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Center Dot", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                CyberSwitch(checked = state.dotEnabled, onCheckedChange = { viewModel.toggleDot() })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("High-Contrast Outline", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                CyberSwitch(checked = state.outlineEnabled, onCheckedChange = { viewModel.toggleOutline() })
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color palette selector
            Text("VORTEX ACCENT COLOR", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                presetColors.forEach { (hex, _) ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = state.colorHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else CyberBorder,
                                shape = CircleShape
                            )
                            .clickable { viewModel.updateColor(hex) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Save Preset, Reset, Launch Floating Crosshair
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CyberButton(
                    text = "Save Preset",
                    onClick = { showSaveDialog = true },
                    icon = Icons.Default.Save,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )
                CyberButton(
                    text = "Reset Default",
                    onClick = { viewModel.resetToDefaults() },
                    icon = Icons.Default.Refresh,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Floating Crosshair Toggle Button
            CyberButton(
                text = if (state.isOverlayActive) "HIDE FLOATING CROSSHAIR" else "LAUNCH FLOATING CROSSHAIR",
                onClick = { viewModel.toggleFloatingCrosshair(context) },
                icon = if (state.isOverlayActive) Icons.Default.VisibilityOff else Icons.Default.CenterFocusStrong,
                modifier = Modifier.fillMaxWidth(),
                isPrimary = !state.isOverlayActive,
                accentColor = if (state.isOverlayActive) StatusWarning else accentColor
            )
        }
    }

    // Save Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = CyberCardElevated,
            title = { Text("SAVE CUSTOM PRESET", color = TextWhite, style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = newPresetName,
                    onValueChange = { newPresetName = it },
                    label = { Text("Preset Name (e.g. My Sniper)", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = accentColor
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPresetName.isNotBlank()) {
                        viewModel.saveAsNewPreset(newPresetName)
                        newPresetName = ""
                        showSaveDialog = false
                    }
                }) {
                    Text("SAVE", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }

    // Permission Dialog
    if (state.showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPermissionDialog() },
            containerColor = CyberCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("FLOATING PERMISSION", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    "Displaying a floating crosshair over games requires the 'Display over other apps' (SYSTEM_ALERT_WINDOW) permission.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissPermissionDialog()
                    PermissionUtils.requestOverlayPermission(context)
                }) {
                    Text("GRANT PERMISSION", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissPermissionDialog() }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }

    // Toast message handler
    if (state.toastMessage != null) {
        LaunchedEffect(state.toastMessage) {
            android.widget.Toast.makeText(context, state.toastMessage, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
}
