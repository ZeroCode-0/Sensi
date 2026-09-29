package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.NexaVortexApplication
import com.example.ui.config.ConfigViewModel
import com.example.ui.crosshair.CrosshairViewModel
import com.example.ui.device.DeviceInfoScreen
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.login.LoginScreen
import com.example.ui.login.LoginViewModel
import com.example.ui.menu.MenuScreen
import com.example.ui.network.NetworkScreen
import com.example.ui.network.NetworkViewModel
import com.example.ui.profiles.GameProfilesScreen
import com.example.ui.profiles.GameProfilesViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.shizuku.ShizukuScreen
import com.example.ui.shizuku.ShizukuViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Dashboard : Screen()
    object Shizuku : Screen()
    object Profiles : Screen()
    object Network : Screen()
    object DeviceInfo : Screen()
}

enum class BottomTab(val label: String, val icon: ImageVector) {
    HOME("HOME", Icons.Default.Dashboard),
    MENU("MENU", Icons.Default.GridView),
    SETTINGS("SETTINGS", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    application: NexaVortexApplication
) {
    val prefs = application.preferencesManager
    val isLoggedIn by prefs.isLoggedIn.collectAsState(initial = false)
    val accentIndex by prefs.accentColorIndex.collectAsState(initial = 0)
    val accentColor = CyberAccent.entries.getOrElse(accentIndex) { CyberAccent.CRIMSON }.color

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedBottomTab by remember { mutableStateOf(BottomTab.HOME) }

    // Splash auto check
    LaunchedEffect(Unit) {
        delay(1200)
        currentScreen = if (isLoggedIn) Screen.Dashboard else Screen.Login
    }

    NexaVortexTheme(accentColor = accentColor) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = CyberBackground
        ) {
            when (val screen = currentScreen) {
                is Screen.Splash -> {
                    SplashScreen()
                }

                is Screen.Login -> {
                    val loginVm: LoginViewModel = viewModel {
                        LoginViewModel(application.preferencesManager)
                    }
                    LoginScreen(
                        viewModel = loginVm,
                        onLoginSuccess = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.Dashboard -> {
                    val homeVm: HomeViewModel = viewModel { HomeViewModel(application) }
                    val crosshairVm: CrosshairViewModel = viewModel { CrosshairViewModel(application) }
                    val configVm: ConfigViewModel = viewModel { ConfigViewModel(application) }
                    val settingsVm: SettingsViewModel = viewModel { SettingsViewModel(application) }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = CyberBackground,
                        bottomBar = {
                            CyberBottomNavigation(
                                selectedTab = selectedBottomTab,
                                onSelectTab = { selectedBottomTab = it }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedBottomTab) {
                                BottomTab.HOME -> {
                                    HomeScreen(
                                        homeViewModel = homeVm,
                                        crosshairViewModel = crosshairVm,
                                        configViewModel = configVm
                                    )
                                }
                                BottomTab.MENU -> {
                                    MenuScreen(
                                        onNavigateToShizuku = { currentScreen = Screen.Shizuku },
                                        onNavigateToProfiles = { currentScreen = Screen.Profiles },
                                        onNavigateToNetwork = { currentScreen = Screen.Network },
                                        onNavigateToDeviceInfo = { currentScreen = Screen.DeviceInfo }
                                    )
                                }
                                BottomTab.SETTINGS -> {
                                    SettingsScreen(
                                        viewModel = settingsVm,
                                        onLogout = { currentScreen = Screen.Login }
                                    )
                                }
                            }
                        }
                    }
                }

                is Screen.Shizuku -> {
                    val shizukuVm: ShizukuViewModel = viewModel { ShizukuViewModel(application) }
                    ShizukuScreen(
                        viewModel = shizukuVm,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.Profiles -> {
                    val profilesVm: GameProfilesViewModel = viewModel { GameProfilesViewModel(application) }
                    GameProfilesScreen(
                        viewModel = profilesVm,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.Network -> {
                    val networkVm: NetworkViewModel = viewModel { NetworkViewModel(application) }
                    NetworkScreen(
                        viewModel = networkVm,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.DeviceInfo -> {
                    DeviceInfoScreen(
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val accentColor = LocalCyberAccent.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(72.dp)
            )
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
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = accentColor,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
fun CyberBottomNavigation(
    selectedTab: BottomTab,
    onSelectTab: (BottomTab) -> Unit
) {
    val accentColor = LocalCyberAccent.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) accentColor else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) accentColor else TextMuted,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
