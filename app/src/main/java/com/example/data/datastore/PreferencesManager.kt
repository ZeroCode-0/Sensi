package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nexa_vortex_settings")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        val USERNAME = stringPreferencesKey("saved_username")
        val ACCENT_COLOR_INDEX = intPreferencesKey("accent_color_index")

        val IS_GAME_MODE_ACTIVE = booleanPreferencesKey("is_game_mode_active")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")

        val FLOATING_MONITOR_ENABLED = booleanPreferencesKey("floating_monitor_enabled")
        val FLOATING_OPACITY = floatPreferencesKey("floating_opacity")
        val FLOATING_SIZE = floatPreferencesKey("floating_size")
        val FLOATING_SHOW_FPS = booleanPreferencesKey("floating_show_fps")
        val FLOATING_SHOW_RAM = booleanPreferencesKey("floating_show_ram")
        val FLOATING_SHOW_TEMP = booleanPreferencesKey("floating_show_temp")
        val FLOATING_SHOW_BATTERY = booleanPreferencesKey("floating_show_battery")

        // Crosshair
        val CROSSHAIR_ENABLED = booleanPreferencesKey("crosshair_enabled")
        val CROSSHAIR_SHAPE = stringPreferencesKey("crosshair_shape")
        val CROSSHAIR_SIZE = floatPreferencesKey("crosshair_size")
        val CROSSHAIR_THICKNESS = floatPreferencesKey("crosshair_thickness")
        val CROSSHAIR_GAP = floatPreferencesKey("crosshair_gap")
        val CROSSHAIR_OPACITY = floatPreferencesKey("crosshair_opacity")
        val CROSSHAIR_COLOR_HEX = stringPreferencesKey("crosshair_color_hex")
        val CROSSHAIR_DOT_ENABLED = booleanPreferencesKey("crosshair_dot_enabled")
        val CROSSHAIR_OUTLINE_ENABLED = booleanPreferencesKey("crosshair_outline_enabled")
        val CROSSHAIR_ROTATION = floatPreferencesKey("crosshair_rotation")
        val CROSSHAIR_OFFSET_X = floatPreferencesKey("crosshair_offset_x")
        val CROSSHAIR_OFFSET_Y = floatPreferencesKey("crosshair_offset_y")

        // Sensitivity
        val SENS_GENERAL = intPreferencesKey("sens_general")
        val SENS_TOUCH = intPreferencesKey("sens_touch")
        val SENS_SWIPE = intPreferencesKey("sens_swipe")
        val SENS_POINTER = intPreferencesKey("sens_pointer")
        val SENS_PRESET = stringPreferencesKey("sens_preset")

        // Display
        val TARGET_REFRESH_RATE = intPreferencesKey("target_refresh_rate")
        val SCREEN_SMOOTHER_LEVEL = intPreferencesKey("screen_smoother_level")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.IS_LOGGED_IN] ?: false }

    val rememberMe: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.REMEMBER_ME] ?: true }

    val savedUsername: Flow<String> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.USERNAME] ?: "admin" }

    val accentColorIndex: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.ACCENT_COLOR_INDEX] ?: 0 }

    val isGameModeActive: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.IS_GAME_MODE_ACTIVE] ?: false }

    val keepScreenAwake: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.KEEP_SCREEN_AWAKE] ?: true }

    val floatingMonitorEnabled: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.FLOATING_MONITOR_ENABLED] ?: false }

    val floatingOpacity: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.FLOATING_OPACITY] ?: 0.88f }

    val crosshairEnabled: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_ENABLED] ?: false }

    val crosshairShape: Flow<String> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_SHAPE] ?: "Classic" }

    val crosshairSize: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_SIZE] ?: 30f }

    val crosshairThickness: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_THICKNESS] ?: 3f }

    val crosshairGap: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_GAP] ?: 8f }

    val crosshairOpacity: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_OPACITY] ?: 1.0f }

    val crosshairColorHex: Flow<String> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_COLOR_HEX] ?: "#FF2A4D" }

    val crosshairDotEnabled: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_DOT_ENABLED] ?: true }

    val crosshairOutlineEnabled: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_OUTLINE_ENABLED] ?: true }

    val crosshairRotation: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_ROTATION] ?: 0f }

    val crosshairOffsetX: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_OFFSET_X] ?: 0f }

    val crosshairOffsetY: Flow<Float> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.CROSSHAIR_OFFSET_Y] ?: 0f }

    val sensGeneral: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.SENS_GENERAL] ?: 65 }

    val sensTouch: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.SENS_TOUCH] ?: 80 }

    val sensSwipe: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.SENS_SWIPE] ?: 70 }

    val sensPointer: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.SENS_POINTER] ?: 60 }

    val sensPreset: Flow<String> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.SENS_PRESET] ?: "BALANCED" }

    val screenSmootherLevel: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.SCREEN_SMOOTHER_LEVEL] ?: 80 }

    val targetRefreshRate: Flow<Int> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[PreferencesKeys.TARGET_REFRESH_RATE] ?: 0 }

    suspend fun setLoginSession(loggedIn: Boolean, remember: Boolean, username: String) {
        context.dataStore.edit {
            it[PreferencesKeys.IS_LOGGED_IN] = loggedIn
            it[PreferencesKeys.REMEMBER_ME] = remember
            it[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[PreferencesKeys.IS_LOGGED_IN] = false
        }
    }

    suspend fun setAccentColorIndex(index: Int) {
        context.dataStore.edit { it[PreferencesKeys.ACCENT_COLOR_INDEX] = index }
    }

    suspend fun setGameModeActive(active: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_GAME_MODE_ACTIVE] = active }
    }

    suspend fun setKeepScreenAwake(keepAwake: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEEP_SCREEN_AWAKE] = keepAwake }
    }

    suspend fun setFloatingMonitorEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.FLOATING_MONITOR_ENABLED] = enabled }
    }

    suspend fun setFloatingOpacity(opacity: Float) {
        context.dataStore.edit { it[PreferencesKeys.FLOATING_OPACITY] = opacity }
    }

    suspend fun setCrosshairEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.CROSSHAIR_ENABLED] = enabled }
    }

    suspend fun updateCrosshairSettings(
        shape: String,
        size: Float,
        thickness: Float,
        gap: Float,
        opacity: Float,
        colorHex: String,
        dotEnabled: Boolean,
        outlineEnabled: Boolean,
        rotation: Float
    ) {
        context.dataStore.edit {
            it[PreferencesKeys.CROSSHAIR_SHAPE] = shape
            it[PreferencesKeys.CROSSHAIR_SIZE] = size
            it[PreferencesKeys.CROSSHAIR_THICKNESS] = thickness
            it[PreferencesKeys.CROSSHAIR_GAP] = gap
            it[PreferencesKeys.CROSSHAIR_OPACITY] = opacity
            it[PreferencesKeys.CROSSHAIR_COLOR_HEX] = colorHex
            it[PreferencesKeys.CROSSHAIR_DOT_ENABLED] = dotEnabled
            it[PreferencesKeys.CROSSHAIR_OUTLINE_ENABLED] = outlineEnabled
            it[PreferencesKeys.CROSSHAIR_ROTATION] = rotation
        }
    }

    suspend fun updateCrosshairOffset(dx: Float, dy: Float) {
        context.dataStore.edit {
            it[PreferencesKeys.CROSSHAIR_OFFSET_X] = dx
            it[PreferencesKeys.CROSSHAIR_OFFSET_Y] = dy
        }
    }

    suspend fun updateSensitivity(general: Int, touch: Int, swipe: Int, pointer: Int, preset: String) {
        context.dataStore.edit {
            it[PreferencesKeys.SENS_GENERAL] = general
            it[PreferencesKeys.SENS_TOUCH] = touch
            it[PreferencesKeys.SENS_SWIPE] = swipe
            it[PreferencesKeys.SENS_POINTER] = pointer
            it[PreferencesKeys.SENS_PRESET] = preset
        }
    }

    suspend fun setScreenSmoother(level: Int) {
        context.dataStore.edit { it[PreferencesKeys.SCREEN_SMOOTHER_LEVEL] = level }
    }

    suspend fun setTargetRefreshRate(rate: Int) {
        context.dataStore.edit { it[PreferencesKeys.TARGET_REFRESH_RATE] = rate }
    }

    suspend fun resetAllSettings() {
        context.dataStore.edit {
            it.clear()
            it[PreferencesKeys.IS_LOGGED_IN] = true // keep login intact unless logout
            it[PreferencesKeys.USERNAME] = "admin"
        }
    }
}
