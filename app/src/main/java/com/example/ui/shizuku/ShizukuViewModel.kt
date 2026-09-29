package com.example.ui.shizuku

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.system.ShizukuCommandResult
import com.example.system.ShizukuManager
import com.example.system.ShizukuStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

data class ShizukuUiState(
    val status: ShizukuStatus = ShizukuStatus.NOT_RUNNING,
    val isInstalled: Boolean = false,
    val isRunningCommand: Boolean = false,
    val commandLog: List<ShizukuCommandResult> = emptyList(),
    val toastMessage: String? = null
)

class ShizukuViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ShizukuUiState())
    val uiState: StateFlow<ShizukuUiState> = _uiState.asStateFlow()

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == ShizukuManager.REQUEST_CODE_SHIZUKU_PERMISSION) {
            checkStatus()
        }
    }

    init {
        checkStatus()
    }

    fun checkStatus() {
        val context = getApplication<Application>().applicationContext
        val status = ShizukuManager.checkStatus(context)
        val installed = ShizukuManager.isShizukuInstalled(context)
        _uiState.value = _uiState.value.copy(
            status = status,
            isInstalled = installed
        )
    }

    fun requestPermission() {
        ShizukuManager.requestPermission(permissionListener)
    }

    fun openShizukuApp(context: Context) {
        ShizukuManager.openShizukuApp(context)
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunningCommand = true)
            val res1 = ShizukuManager.executeAllowlistedCommand("getprop ro.build.version.release")
            val res2 = ShizukuManager.executeAllowlistedCommand("wm size")
            val res3 = ShizukuManager.executeAllowlistedCommand("wm density")

            _uiState.value = _uiState.value.copy(
                isRunningCommand = false,
                commandLog = listOf(res1, res2, res3) + _uiState.value.commandLog,
                toastMessage = "Diagnostics completed."
            )
        }
    }

    fun clearLog() {
        _uiState.value = _uiState.value.copy(commandLog = emptyList())
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
