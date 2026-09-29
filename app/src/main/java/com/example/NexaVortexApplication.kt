package com.example

import android.app.Application
import com.example.data.datastore.PreferencesManager
import com.example.data.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import rikka.shizuku.Shizuku

class NexaVortexApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val preferencesManager by lazy { PreferencesManager(this) }

    override fun onCreate() {
        super.onCreate()
        initShizukuListener()
    }

    private fun initShizukuListener() {
        try {
            Shizuku.addBinderReceivedListenerSticky {
                // Shizuku binder ready
            }
            Shizuku.addBinderDeadListener {
                // Shizuku binder died
            }
        } catch (_: Throwable) {
            // Ignore if Shizuku is not installed or available
        }
    }
}
