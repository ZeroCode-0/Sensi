package com.example.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val accentColor = LocalCyberAccent.current

    // Pulsing logo animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Animated Cyber Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.5.dp, accentColor.copy(alpha = 0.6f), CircleShape)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_nexa_logo),
                    contentDescription = "NEXA VORTEX Emblem",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "NEXA VORTEX",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "PREMIUM GAMING CONTROL CENTER",
                style = MaterialTheme.typography.labelMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Demo Login indicator banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCardElevated.copy(alpha = 0.7f))
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DEMO CREDENTIALS",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "admin • 111222xyz",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }

                    CyberButton(
                        text = "Autofill",
                        onClick = { viewModel.fillDemoCredentials() },
                        modifier = Modifier.height(32.dp),
                        isPrimary = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cyber Login Card
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = accentColor.copy(alpha = 0.4f)
            ) {
                Text(
                    text = "AUTHENTICATION HANDSHAKE",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Username field
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Username", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = accentColor)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = accentColor,
                        focusedContainerColor = CyberBackground,
                        unfocusedContainerColor = CyberBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = accentColor)
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        viewModel.login(onLoginSuccess)
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = accentColor,
                        focusedContainerColor = CyberBackground,
                        unfocusedContainerColor = CyberBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Remember login toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleRememberMe() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = { viewModel.toggleRememberMe() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentColor,
                            uncheckedColor = CyberBorder,
                            checkmarkColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remember Cyber Session",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login Button / Loading
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCardElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = accentColor,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "VERIFYING ENCRYPTION...",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextWhite
                            )
                        }
                    }
                } else if (uiState.isSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusSuccess.copy(alpha = 0.2f))
                            .border(1.dp, StatusSuccess, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACCESS GRANTED",
                                style = MaterialTheme.typography.labelLarge,
                                color = StatusSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    CyberButton(
                        text = "CONNECT TO VORTEX",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.login(onLoginSuccess)
                        },
                        icon = Icons.Default.Bolt,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CONTROL • OPTIMIZE • PLAY",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                letterSpacing = 1.8.sp
            )
        }

        // Invalid credential dialog
        if (uiState.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissError() },
                containerColor = CyberCardElevated,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = StatusError)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AUTHENTICATION FAILURE", color = StatusError, style = MaterialTheme.typography.titleMedium)
                    }
                },
                text = {
                    Text(
                        uiState.errorMessage ?: "",
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissError() }) {
                        Text("TRY AGAIN", color = accentColor, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
