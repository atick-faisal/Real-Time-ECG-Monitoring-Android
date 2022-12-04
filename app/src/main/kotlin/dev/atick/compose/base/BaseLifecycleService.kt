package dev.atick.compose.base

import android.app.Notification
import android.content.Intent
import androidx.lifecycle.LifecycleService

abstract class BaseLifecycleService : LifecycleService() {

    companion object {
        const val PERSISTENT_NOTIFICATION_ID = 101
        const val ACTION_START_SERVICE = "START_SERVICE"
        const val ACTION_STOP_SERVICE = "STOP_SERVICE"
    }

    private lateinit var persistentNotification: Notification

    open fun onCreateService() {}
    abstract fun onStartService(intent: Intent?)
    abstract fun setupNotification(): Notification
    abstract fun collectGarbage()

    override fun onCreate() {
        super.onCreate()
        onCreateService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                onStartService(intent)
                persistentNotification = setupNotification()
                startForeground(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotification
                )
            }
            ACTION_STOP_SERVICE -> stopService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    open fun stopService() {
        collectGarbage()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}