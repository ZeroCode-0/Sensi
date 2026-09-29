package com.example.ui.network

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.system.NetworkInfoState
import com.example.system.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NetworkUiState(
    val info: NetworkInfoState = NetworkInfoState(),
    val isPinging: Boolean = false,
    val selectedServer: String = "8.8.8.8 (Google DNS)",
    val lastPingMs: Long = -1,
    val throughputKbps: Long = 0,
    val toastMessage: String? = null
)

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    init {
        refreshNetworkState()
    }

    fun refreshNetworkState() {
        val context = getApplication<Application>().applicationContext
        val state = NetworkManager.getNetworkState(context)
        _uiState.value = _uiState.value.copy(info = state)
    }

    fun setServer(serverDesc: String) {
        _uiState.value = _uiState.value.copy(selectedServer = serverDesc)
    }

    fun runPingTest() {
        val host = if (_uiState.value.selectedServer.contains("1.1.1.1")) "1.1.1.1" else "8.8.8.8"
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isPinging = true)
            val pingResult = NetworkManager.runPingTest(host = host, port = 53, timeoutMs = 2500)
            val throughput = NetworkManager.sampleThroughputKbps(1000)

            _uiState.value = _uiState.value.copy(
                isPinging = false,
                lastPingMs = pingResult,
                throughputKbps = throughput,
                toastMessage = if (pingResult > 0) "Ping Test: ${pingResult}ms" else "Ping failed or host unreachable."
            )
            refreshNetworkState()
        }
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
