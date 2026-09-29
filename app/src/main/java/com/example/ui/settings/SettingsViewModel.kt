package com.example.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NexaVortexApplication
import com.example.system.ShizukuManager
import com.example.system.ShizukuStatus
import com.example.ui.theme.CyberAccent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val username: String = "admin",
    val accentIndex: Int = 0,
    val isGameModeActive: Boolean = false,
    val keepScreenAwake: Boolean = true,
    val floatingMonitorEnabled: Boolean = false,
    val floatingOpacity: Float = 0.88f,
    val shizukuStatus: ShizukuStatus = ShizukuStatus.NOT_RUNNING,
    val showResetAllDialog: Boolean = false,
    val toastMessage: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NexaVortexApplication
    private val prefs = app.preferencesManager
    private val db = app.database

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.savedUsername.collect { user ->
                _uiState.value = _uiState.value.copy(username = user)
            }
        }
        viewModelScope.launch {
            prefs.accentColorIndex.collect { idx ->
                _uiState.value = _uiState.value.copy(accentIndex = idx)
            }
        }
        viewModelScope.launch {
            prefs.isGameModeActive.collect { act ->
                _uiState.value = _uiState.value.copy(isGameModeActive = act)
            }
        }
        viewModelScope.launch {
            prefs.keepScreenAwake.collect { kw ->
                _uiState.value = _uiState.value.copy(keepScreenAwake = kw)
            }
        }
        viewModelScope.launch {
            prefs.floatingMonitorEnabled.collect { fe ->
                _uiState.value = _uiState.value.copy(floatingMonitorEnabled = fe)
            }
        }
        viewModelScope.launch {
            prefs.floatingOpacity.collect { op ->
                _uiState.value = _uiState.value.copy(floatingOpacity = op)
            }
        }

        checkShizuku()
    }

    fun checkShizuku() {
        val status = ShizukuManager.checkStatus(getApplication<Application>().applicationContext)
        _uiState.value = _uiState.value.copy(shizukuStatus = status)
    }

    fun setAccentColor(index: Int) {
        viewModelScope.launch {
            prefs.setAccentColorIndex(index)
            _uiState.value = _uiState.value.copy(toastMessage = "Theme accent updated!")
        }
    }

    fun toggleKeepAwake(keep: Boolean) {
        viewModelScope.launch {
            prefs.setKeepScreenAwake(keep)
        }
    }

    fun setFloatingOpacity(op: Float) {
        _uiState.value = _uiState.value.copy(floatingOpacity = op)
        viewModelScope.launch {
            prefs.setFloatingOpacity(op)
        }
    }

    fun promptResetAll() {
        _uiState.value = _uiState.value.copy(showResetAllDialog = true)
    }

    fun dismissResetDialog() {
        _uiState.value = _uiState.value.copy(showResetAllDialog = false)
    }

    fun confirmResetAll() {
        dismissResetDialog()
        viewModelScope.launch {
            prefs.resetAllSettings()
            db.gameProfileDao().deactivateAll()
            _uiState.value = _uiState.value.copy(toastMessage = "All vortex preferences restored to factory defaults.")
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            prefs.logout()
            onLoggedOut()
        }
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
