package com.example.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "sensitivity_presets")
data class SensitivityPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val generalSens: Int,
    val touchResponse: Int,
    val swipeSpeed: Int,
    val pointerSpeed: Int,
    val isSystemPreset: Boolean = false
)

@Dao
interface SensitivityDao {
    @Query("SELECT * FROM sensitivity_presets ORDER BY isSystemPreset DESC, name ASC")
    fun getAllPresets(): Flow<List<SensitivityPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: SensitivityPresetEntity): Long

    @Delete
    suspend fun deletePreset(preset: SensitivityPresetEntity)

    @Query("SELECT COUNT(*) FROM sensitivity_presets")
    suspend fun count(): Int
}
