package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        GameProfileEntity::class,
        SensitivityPresetEntity::class,
        CrosshairPresetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameProfileDao(): GameProfileDao
    abstract fun sensitivityDao(): SensitivityDao
    abstract fun crosshairDao(): CrosshairDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexa_vortex_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(db: AppDatabase) {
                val sensDao = db.sensitivityDao()
                if (sensDao.count() == 0) {
                    sensDao.insertPreset(SensitivityPresetEntity(name = "LOW", generalSens = 40, touchResponse = 50, swipeSpeed = 45, pointerSpeed = 40, isSystemPreset = true))
                    sensDao.insertPreset(SensitivityPresetEntity(name = "BALANCED", generalSens = 65, touchResponse = 80, swipeSpeed = 70, pointerSpeed = 60, isSystemPreset = true))
                    sensDao.insertPreset(SensitivityPresetEntity(name = "FAST", generalSens = 90, touchResponse = 95, swipeSpeed = 90, pointerSpeed = 85, isSystemPreset = true))
                }

                val crosshairDao = db.crosshairDao()
                if (crosshairDao.count() == 0) {
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Classic", shape = "Classic", size = 28f, thickness = 3f, gap = 8f, opacity = 1.0f, colorHex = "#FF2A4D", dotEnabled = true, outlineEnabled = true, rotation = 0f, isSystemPreset = true))
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Dot", shape = "Dot", size = 12f, thickness = 6f, gap = 0f, opacity = 1.0f, colorHex = "#00FF88", dotEnabled = true, outlineEnabled = true, rotation = 0f, isSystemPreset = true))
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Circle", shape = "Circle", size = 32f, thickness = 2.5f, gap = 12f, opacity = 0.9f, colorHex = "#00E5FF", dotEnabled = true, outlineEnabled = true, rotation = 0f, isSystemPreset = true))
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Plus", shape = "Plus", size = 24f, thickness = 4f, gap = 4f, opacity = 1.0f, colorHex = "#FFFF00", dotEnabled = false, outlineEnabled = true, rotation = 0f, isSystemPreset = true))
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Minimal", shape = "Minimal", size = 18f, thickness = 2f, gap = 6f, opacity = 0.85f, colorHex = "#FFFFFF", dotEnabled = true, outlineEnabled = false, rotation = 0f, isSystemPreset = true))
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Precision", shape = "Precision", size = 34f, thickness = 2f, gap = 10f, opacity = 1.0f, colorHex = "#FF2A4D", dotEnabled = true, outlineEnabled = true, rotation = 0f, isSystemPreset = true))
                    crosshairDao.insertPreset(CrosshairPresetEntity(name = "Vortex", shape = "Vortex", size = 36f, thickness = 3f, gap = 8f, opacity = 1.0f, colorHex = "#B026FF", dotEnabled = true, outlineEnabled = true, rotation = 45f, isSystemPreset = true))
                }

                val gameProfileDao = db.gameProfileDao()
                gameProfileDao.insertProfile(
                    GameProfileEntity(
                        profileName = "Apex FPS Pro",
                        gameName = "Battle Royale Pro",
                        packageName = "com.sample.shooter",
                        refreshRatePreference = 120,
                        brightnessPercent = 90,
                        keepAwake = true,
                        floatingMonitor = true,
                        crosshairPreset = "Precision",
                        isActivated = false
                    )
                )
            }
        }
    }
}
