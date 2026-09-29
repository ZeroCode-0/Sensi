package com.example.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.HapticUtils

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = LocalCyberAccent.current.copy(alpha = 0.35f),
    backgroundColor: Color = CyberCard,
    cornerRadius: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    accentColor: Color = LocalCyberAccent.current,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "btnScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isPrimary) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (enabled) 0.95f else 0.4f),
                            accentColor.copy(alpha = if (enabled) 0.75f else 0.3f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            CyberCardElevated,
                            CyberCard
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (isPrimary) accentColor else accentColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                HapticUtils.vibrateShort(context)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) Color.Black else TextWhite,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = if (isPrimary) Color.Black else TextWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun CyberBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalCyberAccent.current
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun CyberProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = LocalCyberAccent.current,
    trackColor: Color = CyberBorder
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.7f),
                            color
                        )
                    )
                )
        )
    }
}

@Composable
fun CyberSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    accentColor: Color = LocalCyberAccent.current
) {
    val context = LocalContext.current
    Slider(
        value = value,
        onValueChange = {
            HapticUtils.vibrateShort(context)
            onValueChange(it)
        },
        valueRange = valueRange,
        steps = steps,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = accentColor,
            activeTrackColor = accentColor,
            inactiveTrackColor = CyberBorder
        )
    )
}

@Composable
fun CyberSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalCyberAccent.current
) {
    val context = LocalContext.current
    Switch(
        checked = checked,
        onCheckedChange = {
            HapticUtils.vibrateShort(context)
            onCheckedChange(it)
        },
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Black,
            checkedTrackColor = accentColor,
            uncheckedThumbColor = TextGray,
            uncheckedTrackColor = CyberBorder,
            uncheckedBorderColor = CyberBorder
        )
    )
}

@Composable
fun CyberSectionHeader(
    title: String,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LocalCyberAccent.current)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        if (badge != null) {
            CyberBadge(text = badge)
        }
    }
}
