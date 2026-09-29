package com.example.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NexaVortexApplication
import com.example.service.PerformanceService
import com.example.system.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeDashboardState(
    val selectedTab: Int = 0, // 0: MAIN MENU, 1: CROSSHAIR, 2: CONFIG
    val isGameModeActive: Boolean = false,
    val keepAwake: Boolean = true,
    val ramInfo: RamInfo = RamInfo(0.0, 0.0, 0.0, 0),
    val cpuPercent: Int = 24,
    val batteryDetails: BatteryDetails = BatteryDetails(0, false, 0f, 0, "Normal", "Li-ion"),
    val displayDetails: DisplayDetails = DisplayDetails(1080, 2400, 420, 60f, listOf(60f)),
    val storageInfo: StorageInfo = StorageInfo(0.0, 0.0, 0.0, 0),
    val networkState: NetworkInfoState = NetworkInfoState(),
    val liveFps: Int = 60,
    val toastMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NexaVortexApplication
    private val prefs = app.preferencesManager

    private val _uiState = MutableStateFlow(HomeDashboardState())
    val uiState: StateFlow<HomeDashboardState> = _uiState.asStateFlow()

    init {
        // Observe preferences
        viewModelScope.launch {
            prefs.isGameModeActive.collect { active ->
                _uiState.value = _uiState.value.copy(isGameModeActive = active)
            }
        }
        viewModelScope.launch {
            prefs.keepScreenAwake.collect { awake ->
                _uiState.value = _uiState.value.copy(keepAwake = awake)
            }
        }
        viewModelScope.launch {
            PerformanceManager.liveMetrics.collect { metrics ->
                _uiState.value = _uiState.value.copy(
                    liveFps = metrics.fps,
                    cpuPercent = metrics.cpuPercent
                )
            }
        }

        // Start real-time monitoring loop
        startTelemetryLoop()
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun toggleGameMode(context: Context) {
        val nextState = !_uiState.value.isGameModeActive
        viewModelScope.launch {
            prefs.setGameModeActive(nextState)
            if (nextState) {
                PerformanceService.startService(context)
                _uiState.value = _uiState.value.copy(toastMessage = "Game Mode Activated • Telemetry service started")
            } else {
                PerformanceService.stopService(context)
                _uiState.value = _uiState.value.copy(toastMessage = "Game Mode Deactivated")
            }
        }
    }

    fun toggleKeepAwake(keep: Boolean) {
        viewModelScope.launch {
            prefs.setKeepScreenAwake(keep)
        }
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun refreshStats() {
        fetchHardwareStats()
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                fetchHardwareStats()
                delay(2000)
            }
        }
    }

    private fun fetchHardwareStats() {
        val context = getApplication<Application>().applicationContext
        val ram = DeviceInfoManager.getRamInfo(context)
        val storage = DeviceInfoManager.getStorageInfo()
        val battery = DeviceInfoManager.getBatteryDetails(context)
        val display = DeviceInfoManager.getDisplayDetails(context)
        val network = NetworkManager.getNetworkState(context)
        val cpu = PerformanceManager.readCpuUsage()

        _uiState.value = _uiState.value.copy(
            ramInfo = ram,
            storageInfo = storage,
            batteryDetails = battery,
            displayDetails = display,
            networkState = network,
            cpuPercent = cpu
        )
    }
}
