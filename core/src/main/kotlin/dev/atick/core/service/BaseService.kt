package dev.atick.core.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LifecycleService

abstract class BaseService : LifecycleService() {

    companion object {
        const val PERSISTENT_NOTIFICATION_ID = 101
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
        onStartService(intent)
        persistentNotification = setupNotification()
        startForeground(PERSISTENT_NOTIFICATION_ID, persistentNotification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        collectGarbage()
        super.onDestroy()
    }

    open fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }
}