package com.example.ui.crosshair

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NexaVortexApplication
import com.example.data.room.CrosshairPresetEntity
import com.example.service.FloatingMonitorService
import com.example.utils.PermissionUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CrosshairState(
    val shape: String = "Classic",
    val size: Float = 28f,
    val thickness: Float = 3f,
    val gap: Float = 8f,
    val opacity: Float = 1.0f,
    val colorHex: String = "#FF2A4D",
    val dotEnabled: Boolean = true,
    val outlineEnabled: Boolean = true,
    val rotation: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val isOverlayActive: Boolean = false,
    val presets: List<CrosshairPresetEntity> = emptyList(),
    val showPermissionDialog: Boolean = false,
    val toastMessage: String? = null
)

class CrosshairViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NexaVortexApplication
    private val prefs = app.preferencesManager
    private val db = app.database

    private val _uiState = MutableStateFlow(CrosshairState())
    val uiState: StateFlow<CrosshairState> = _uiState.asStateFlow()

    init {
        // Load presets from Room
        viewModelScope.launch {
            db.crosshairDao().getAllPresets().collect { list ->
                _uiState.value = _uiState.value.copy(presets = list)
            }
        }

        // Collect preferences independently
        viewModelScope.launch {
            prefs.crosshairShape.collect { s -> _uiState.value = _uiState.value.copy(shape = s) }
        }
        viewModelScope.launch {
            prefs.crosshairSize.collect { s -> _uiState.value = _uiState.value.copy(size = s) }
        }
        viewModelScope.launch {
            prefs.crosshairThickness.collect { t -> _uiState.value = _uiState.value.copy(thickness = t) }
        }
        viewModelScope.launch {
            prefs.crosshairGap.collect { g -> _uiState.value = _uiState.value.copy(gap = g) }
        }
        viewModelScope.launch {
            prefs.crosshairOpacity.collect { o -> _uiState.value = _uiState.value.copy(opacity = o) }
        }
        viewModelScope.launch {
            prefs.crosshairColorHex.collect { c -> _uiState.value = _uiState.value.copy(colorHex = c) }
        }
        viewModelScope.launch {
            prefs.crosshairDotEnabled.collect { dot ->
                _uiState.value = _uiState.value.copy(dotEnabled = dot)
            }
        }
        viewModelScope.launch {
            prefs.crosshairOutlineEnabled.collect { outline ->
                _uiState.value = _uiState.value.copy(outlineEnabled = outline)
            }
        }
        viewModelScope.launch {
            prefs.crosshairRotation.collect { rot ->
                _uiState.value = _uiState.value.copy(rotation = rot)
            }
        }
        viewModelScope.launch {
            prefs.crosshairOffsetX.collect { ox ->
                _uiState.value = _uiState.value.copy(offsetX = ox)
            }
        }
        viewModelScope.launch {
            prefs.crosshairOffsetY.collect { oy ->
                _uiState.value = _uiState.value.copy(offsetY = oy)
            }
        }
    }

    fun updateSize(newVal: Float) {
        _uiState.value = _uiState.value.copy(size = newVal)
        persistCrosshair()
    }

    fun updateThickness(newVal: Float) {
        _uiState.value = _uiState.value.copy(thickness = newVal)
        persistCrosshair()
    }

    fun updateGap(newVal: Float) {
        _uiState.value = _uiState.value.copy(gap = newVal)
        persistCrosshair()
    }

    fun updateOpacity(newVal: Float) {
        _uiState.value = _uiState.value.copy(opacity = newVal)
        persistCrosshair()
    }

    fun updateRotation(newVal: Float) {
        _uiState.value = _uiState.value.copy(rotation = newVal)
        persistCrosshair()
    }

    fun updateColor(hex: String) {
        _uiState.value = _uiState.value.copy(colorHex = hex)
        persistCrosshair()
    }

    fun toggleDot() {
        val next = !_uiState.value.dotEnabled
        _uiState.value = _uiState.value.copy(dotEnabled = next)
        persistCrosshair()
    }

    fun toggleOutline() {
        val next = !_uiState.value.outlineEnabled
        _uiState.value = _uiState.value.copy(outlineEnabled = next)
        persistCrosshair()
    }

    fun adjustOffset(dx: Float, dy: Float) {
        val newX = (_uiState.value.offsetX + dx).coerceIn(-50f, 50f)
        val newY = (_uiState.value.offsetY + dy).coerceIn(-50f, 50f)
        _uiState.value = _uiState.value.copy(offsetX = newX, offsetY = newY)
        viewModelScope.launch {
            prefs.updateCrosshairOffset(newX, newY)
        }
    }

    fun resetOffset() {
        _uiState.value = _uiState.value.copy(offsetX = 0f, offsetY = 0f)
        viewModelScope.launch {
            prefs.updateCrosshairOffset(0f, 0f)
        }
    }

    fun applyPreset(preset: CrosshairPresetEntity) {
        _uiState.value = _uiState.value.copy(
            shape = preset.shape,
            size = preset.size,
            thickness = preset.thickness,
            gap = preset.gap,
            opacity = preset.opacity,
            colorHex = preset.colorHex,
            dotEnabled = preset.dotEnabled,
            outlineEnabled = preset.outlineEnabled,
            rotation = preset.rotation
        )
        persistCrosshair()
    }

    fun saveAsNewPreset(name: String) {
        if (name.isBlank()) return
        val current = _uiState.value
        viewModelScope.launch {
            db.crosshairDao().insertPreset(
                CrosshairPresetEntity(
                    name = name.trim(),
                    shape = current.shape,
                    size = current.size,
                    thickness = current.thickness,
                    gap = current.gap,
                    opacity = current.opacity,
                    colorHex = current.colorHex,
                    dotEnabled = current.dotEnabled,
                    outlineEnabled = current.outlineEnabled,
                    rotation = current.rotation,
                    isSystemPreset = false
                )
            )
            _uiState.value = _uiState.value.copy(toastMessage = "Preset '$name' saved to Vortex profile!")
        }
    }

    fun deletePreset(preset: CrosshairPresetEntity) {
        viewModelScope.launch {
            db.crosshairDao().deletePreset(preset)
            _uiState.value = _uiState.value.copy(toastMessage = "Preset '${preset.name}' deleted.")
        }
    }

    fun toggleFloatingCrosshair(context: Context) {
        if (!PermissionUtils.canDrawOverlays(context)) {
            _uiState.value = _uiState.value.copy(showPermissionDialog = true)
            return
        }

        val next = !_uiState.value.isOverlayActive
        _uiState.value = _uiState.value.copy(isOverlayActive = next)

        if (next) {
            FloatingMonitorService.startCrosshair(context)
            _uiState.value = _uiState.value.copy(toastMessage = "Floating Crosshair Activated on screen")
        } else {
            FloatingMonitorService.stop(context)
            _uiState.value = _uiState.value.copy(toastMessage = "Floating Crosshair Removed")
        }
    }

    fun dismissPermissionDialog() {
        _uiState.value = _uiState.value.copy(showPermissionDialog = false)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun resetToDefaults() {
        _uiState.value = _uiState.value.copy(
            shape = "Classic",
            size = 28f,
            thickness = 3f,
            gap = 8f,
            opacity = 1.0f,
            colorHex = "#FF2A4D",
            dotEnabled = true,
            outlineEnabled = true,
            rotation = 0f,
            offsetX = 0f,
            offsetY = 0f
        )
        persistCrosshair()
        resetOffset()
    }

    private fun persistCrosshair() {
        val s = _uiState.value
        viewModelScope.launch {
            prefs.updateCrosshairSettings(
                shape = s.shape,
                size = s.size,
                thickness = s.thickness,
                gap = s.gap,
                opacity = s.opacity,
                colorHex = s.colorHex,
                dotEnabled = s.dotEnabled,
                outlineEnabled = s.outlineEnabled,
                rotation = s.rotation
            )
        }
    }
}
