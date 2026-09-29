package com.example.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_profiles")
data class GameProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileName: String,
    val gameName: String,
    val packageName: String,
    val refreshRatePreference: Int = 120,
    val brightnessPercent: Int = 85,
    val keepAwake: Boolean = true,
    val floatingMonitor: Boolean = true,
    val crosshairPreset: String = "Classic",
    val isActivated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface GameProfileDao {
    @Query("SELECT * FROM game_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<GameProfileEntity>>

    @Query("SELECT * FROM game_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): GameProfileEntity?

    @Query("SELECT * FROM game_profiles WHERE isActivated = 1 LIMIT 1")
    fun getActiveProfile(): Flow<GameProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: GameProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: GameProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: GameProfileEntity)

    @Query("UPDATE game_profiles SET isActivated = CASE WHEN id = :profileId THEN 1 ELSE 0 END")
    suspend fun setActiveProfile(profileId: Long)

    @Query("UPDATE game_profiles SET isActivated = 0")
    suspend fun deactivateAll()
}
