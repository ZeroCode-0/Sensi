package com.example.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "crosshair_presets")
data class CrosshairPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val shape: String,
    val size: Float,
    val thickness: Float,
    val gap: Float,
    val opacity: Float,
    val colorHex: String,
    val dotEnabled: Boolean,
    val outlineEnabled: Boolean,
    val rotation: Float,
    val isSystemPreset: Boolean = false
)

@Dao
interface CrosshairDao {
    @Query("SELECT * FROM crosshair_presets ORDER BY isSystemPreset DESC, name ASC")
    fun getAllPresets(): Flow<List<CrosshairPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: CrosshairPresetEntity): Long

    @Delete
    suspend fun deletePreset(preset: CrosshairPresetEntity)

    @Query("SELECT COUNT(*) FROM crosshair_presets")
    suspend fun count(): Int
}
