package com.example.ui.profiles

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NexaVortexApplication
import com.example.data.room.GameProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InstalledApp(
    val label: String,
    val packageName: String
)

data class GameProfilesState(
    val profiles: List<GameProfileEntity> = emptyList(),
    val installedApps: List<InstalledApp> = emptyList(),
    val activeProfile: GameProfileEntity? = null,
    val showCreateDialog: Boolean = false,
    val toastMessage: String? = null
)

class GameProfilesViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NexaVortexApplication
    private val db = app.database

    private val _uiState = MutableStateFlow(GameProfilesState())
    val uiState: StateFlow<GameProfilesState> = _uiState.asStateFlow()

    init {
        // Observe profiles
        viewModelScope.launch {
            db.gameProfileDao().getAllProfiles().collect { list ->
                val active = list.firstOrNull { it.isActivated }
                _uiState.value = _uiState.value.copy(profiles = list, activeProfile = active)
            }
        }

        // Query launchable apps
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            val apps = resolveInfos.map {
                InstalledApp(
                    label = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName
                )
            }.sortedBy { it.label }

            _uiState.value = _uiState.value.copy(installedApps = apps)
        }
    }

    fun openCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun closeCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun createProfile(
        profileName: String,
        gameName: String,
        packageName: String,
        refreshRate: Int,
        brightness: Int,
        keepAwake: Boolean,
        floatingMonitor: Boolean,
        crosshair: String
    ) {
        if (profileName.isBlank() || gameName.isBlank()) {
            _uiState.value = _uiState.value.copy(toastMessage = "Profile and Game names are required.")
            return
        }

        viewModelScope.launch {
            db.gameProfileDao().insertProfile(
                GameProfileEntity(
                    profileName = profileName.trim(),
                    gameName = gameName.trim(),
                    packageName = packageName.trim().ifBlank { "com.custom.game" },
                    refreshRatePreference = refreshRate,
                    brightnessPercent = brightness,
                    keepAwake = keepAwake,
                    floatingMonitor = floatingMonitor,
                    crosshairPreset = crosshair,
                    isActivated = false
                )
            )
            closeCreateDialog()
            _uiState.value = _uiState.value.copy(toastMessage = "Profile '$profileName' created successfully!")
        }
    }

    fun activateProfile(profile: GameProfileEntity) {
        viewModelScope.launch {
            db.gameProfileDao().setActiveProfile(profile.id)
            _uiState.value = _uiState.value.copy(toastMessage = "Profile '${profile.profileName}' activated!")
        }
    }

    fun deactivateAll() {
        viewModelScope.launch {
            db.gameProfileDao().deactivateAll()
            _uiState.value = _uiState.value.copy(toastMessage = "Game profile deactivated.")
        }
    }

    fun duplicateProfile(profile: GameProfileEntity) {
        viewModelScope.launch {
            db.gameProfileDao().insertProfile(
                profile.copy(
                    id = 0,
                    profileName = "${profile.profileName} (Copy)",
                    isActivated = false,
                    createdAt = System.currentTimeMillis()
                )
            )
            _uiState.value = _uiState.value.copy(toastMessage = "Profile duplicated!")
        }
    }

    fun deleteProfile(profile: GameProfileEntity) {
        viewModelScope.launch {
            db.gameProfileDao().deleteProfile(profile)
            _uiState.value = _uiState.value.copy(toastMessage = "Profile '${profile.profileName}' deleted.")
        }
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
