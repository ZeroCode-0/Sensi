package com.example.ui.config

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NexaVortexApplication
import com.example.system.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ConfigState(
    // Sensitivity Lab
    val generalSens: Int = 65,
    val touchResponse: Int = 80,
    val swipeSpeed: Int = 70,
    val pointerSpeed: Int = 60,
    val activePreset: String = "BALANCED",

    // Display & Resolution
    val currentWidth: Int = 1080,
    val currentHeight: Int = 2400,
    val currentDensity: Int = 420,
    val currentRefreshRate: Float = 120f,
    val supportedRefreshRates: List<Float> = listOf(60f, 120f),

    // Custom Resolution Inputs
    val targetWidthInput: String = "1080",
    val targetHeightInput: String = "2400",
    val targetDensityInput: String = "420",

    // Screen Smoother
    val screenSmootherLevel: Int = 80,
    val selectedRefreshRate: Int = 120,

    // Shizuku
    val shizukuStatus: ShizukuStatus = ShizukuStatus.NOT_RUNNING,
    val isExecutingCommand: Boolean = false,
    val lastExecutionResult: ShizukuCommandResult? = null,
    val showConfirmApplyDialog: Boolean = false,
    val toastMessage: String? = null
)

class ConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NexaVortexApplication
    private val prefs = app.preferencesManager

    private val _uiState = MutableStateFlow(ConfigState())
    val uiState: StateFlow<ConfigState> = _uiState.asStateFlow()

    init {
        loadDisplayInfo()
        loadPreferences()
        checkShizuku()
    }

    private fun loadDisplayInfo() {
        val context = getApplication<Application>().applicationContext
        val display = DeviceInfoManager.getDisplayDetails(context)
        _uiState.value = _uiState.value.copy(
            currentWidth = display.width,
            currentHeight = display.height,
            currentDensity = display.densityDpi,
            currentRefreshRate = display.currentRefreshRate,
            supportedRefreshRates = display.supportedRefreshRates,
            targetWidthInput = display.width.toString(),
            targetHeightInput = display.height.toString(),
            targetDensityInput = display.densityDpi.toString(),
            selectedRefreshRate = display.currentRefreshRate.toInt()
        )
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            combine(
                prefs.sensGeneral,
                prefs.sensTouch,
                prefs.sensSwipe,
                prefs.sensPointer,
                prefs.sensPreset
            ) { g, t, s, p, preset ->
                _uiState.value.copy(
                    generalSens = g,
                    touchResponse = t,
                    swipeSpeed = s,
                    pointerSpeed = p,
                    activePreset = preset
                )
            }.collect { partial ->
                _uiState.value = _uiState.value.copy(
                    generalSens = partial.generalSens,
                    touchResponse = partial.touchResponse,
                    swipeSpeed = partial.swipeSpeed,
                    pointerSpeed = partial.pointerSpeed,
                    activePreset = partial.activePreset
                )
            }
        }

        viewModelScope.launch {
            prefs.screenSmootherLevel.collect { level ->
                _uiState.value = _uiState.value.copy(screenSmootherLevel = level)
            }
        }
    }

    fun checkShizuku() {
        val context = getApplication<Application>().applicationContext
        val status = ShizukuManager.checkStatus(context)
        _uiState.value = _uiState.value.copy(shizukuStatus = status)
    }

    fun updateSensitivity(g: Int, t: Int, s: Int, p: Int, preset: String = "CUSTOM") {
        _uiState.value = _uiState.value.copy(
            generalSens = g,
            touchResponse = t,
            swipeSpeed = s,
            pointerSpeed = p,
            activePreset = preset
        )
        viewModelScope.launch {
            prefs.updateSensitivity(g, t, s, p, preset)
        }
    }

    fun applyPreset(preset: String) {
        when (preset) {
            "LOW" -> updateSensitivity(40, 50, 45, 40, "LOW")
            "BALANCED" -> updateSensitivity(65, 80, 70, 60, "BALANCED")
            "FAST" -> updateSensitivity(90, 95, 90, 85, "FAST")
        }
    }

    fun updateScreenSmoother(level: Int) {
        _uiState.value = _uiState.value.copy(screenSmootherLevel = level)
        viewModelScope.launch {
            prefs.setScreenSmoother(level)
        }
    }

    fun setTargetRefreshRate(rate: Int) {
        _uiState.value = _uiState.value.copy(selectedRefreshRate = rate)
        viewModelScope.launch {
            prefs.setTargetRefreshRate(rate)
            if (_uiState.value.shizukuStatus == ShizukuStatus.CONNECTED) {
                ShizukuManager.executeAllowlistedCommand("settings put system peak_refresh_rate $rate")
                ShizukuManager.executeAllowlistedCommand("settings put system min_refresh_rate $rate")
            }
        }
    }

    fun onWidthInputChange(v: String) { _uiState.value = _uiState.value.copy(targetWidthInput = v) }
    fun onHeightInputChange(v: String) { _uiState.value = _uiState.value.copy(targetHeightInput = v) }
    fun onDensityInputChange(v: String) { _uiState.value = _uiState.value.copy(targetDensityInput = v) }

    fun showConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmApplyDialog = true)
    }

    fun dismissConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmApplyDialog = false)
    }

    fun applyResolutionAndDensity() {
        dismissConfirmDialog()
        val w = _uiState.value.targetWidthInput.toIntOrNull()
        val h = _uiState.value.targetHeightInput.toIntOrNull()
        val d = _uiState.value.targetDensityInput.toIntOrNull()

        if (w == null || h == null || d == null || w < 480 || h < 800 || d < 120) {
            _uiState.value = _uiState.value.copy(toastMessage = "Invalid resolution/density dimensions.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExecutingCommand = true)
            if (_uiState.value.shizukuStatus == ShizukuStatus.CONNECTED) {
                val sizeRes = ShizukuManager.executeAllowlistedCommand("wm size ${w}x$h")
                val densRes = ShizukuManager.executeAllowlistedCommand("wm density $d")

                _uiState.value = _uiState.value.copy(
                    isExecutingCommand = false,
                    lastExecutionResult = densRes,
                    toastMessage = if (sizeRes.isSuccess && densRes.isSuccess) "Display resolution updated successfully!" else "Operation failed via Shizuku."
                )
                loadDisplayInfo()
            } else {
                _uiState.value = _uiState.value.copy(
                    isExecutingCommand = false,
                    toastMessage = "Shizuku permission required for system display change."
                )
            }
        }
    }

    fun resetResolutionAndDensity() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExecutingCommand = true)
            if (_uiState.value.shizukuStatus == ShizukuStatus.CONNECTED) {
                ShizukuManager.executeAllowlistedCommand("wm size reset")
                ShizukuManager.executeAllowlistedCommand("wm density reset")
                _uiState.value = _uiState.value.copy(
                    isExecutingCommand = false,
                    toastMessage = "Display restored to stock native values."
                )
                loadDisplayInfo()
            } else {
                loadDisplayInfo()
                _uiState.value = _uiState.value.copy(
                    isExecutingCommand = false,
                    toastMessage = "Display inputs reset to detected native values."
                )
            }
        }
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
