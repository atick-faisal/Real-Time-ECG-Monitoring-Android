package dev.atick.core.service

import android.app.Notification
import android.app.Service
import android.content.Intent

abstract class BaseService : Service() {

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
        return START_STICKY
    }

    override fun onDestroy() {
        collectGarbage()
        super.onDestroy()
    }
}