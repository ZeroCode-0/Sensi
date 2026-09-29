package com.example.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.savedUsername.collect { user ->
                _uiState.value = _uiState.value.copy(username = user)
            }
        }
    }

    fun onUsernameChange(newVal: String) {
        _uiState.value = _uiState.value.copy(username = newVal, errorMessage = null)
    }

    fun onPasswordChange(newVal: String) {
        _uiState.value = _uiState.value.copy(password = newVal, errorMessage = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun toggleRememberMe() {
        _uiState.value = _uiState.value.copy(rememberMe = !_uiState.value.rememberMe)
    }

    fun fillDemoCredentials() {
        _uiState.value = _uiState.value.copy(
            username = "admin",
            password = "111222xyz",
            errorMessage = null
        )
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun login(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Please enter both username and password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, errorMessage = null)
            // Cyber authentication handshake simulation
            delay(900)

            if (current.username.trim() == "admin" && current.password == "111222xyz") {
                preferencesManager.setLoginSession(
                    loggedIn = true,
                    remember = current.rememberMe,
                    username = current.username.trim()
                )
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                delay(600)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Access Denied: Invalid cyber credentials. Use demo credentials (admin / 111222xyz)."
                )
            }
        }
    }
}
