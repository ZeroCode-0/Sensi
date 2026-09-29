package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Background and Surface Palette (Dark Cyber)
val CyberBackground = Color(0xFF0A0B0F)
val CyberSurface = Color(0xFF11131B)
val CyberCard = Color(0xFF161924)
val CyberCardElevated = Color(0xFF1E2232)
val CyberBorder = Color(0xFF262C3E)
val CyberBorderGlow = Color(0x33FF2A4D)

// Primary Accent Options
val NeonCrimson = Color(0xFFFF2A4D)
val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFFB026FF)
val NeonGreen = Color(0xFF00FF88)
val NeonYellow = Color(0xFFFFD600)

// Text and Status Colors
val TextWhite = Color(0xFFF2F4F8)
val TextGray = Color(0xFF9096A8)
val TextMuted = Color(0xFF5D6478)

val StatusSuccess = Color(0xFF00E676)
val StatusWarning = Color(0xFFFFAB00)
val StatusError = Color(0xFFFF3D71)
val StatusInfo = Color(0xFF00B0FF)

enum class CyberAccent(val displayName: String, val color: Color) {
    CRIMSON("Crimson Red", NeonCrimson),
    CYAN("Cyber Cyan", NeonCyan),
    PURPLE("Neon Purple", NeonPurple),
    GREEN("Acid Green", NeonGreen)
}
