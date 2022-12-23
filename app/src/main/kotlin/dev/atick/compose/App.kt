package dev.atick.compose

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.orhanobut.logger.LogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import dev.atick.compose.service.CardiacZoneService
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var logAdapter: LogAdapter

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(logAdapter)
        Logger.i("SKYNET INITIATED!")
        createPersistentNotificationChannel()
        createAlertNotificationChannel()
    }

    private fun createPersistentNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CardiacZoneService.PERSISTENT_NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_description)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
            as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createAlertNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CardiacZoneService.ALERT_NOTIFICATION_CHANNEL_ID,
            getString(R.string.alert_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.alert_notification_channel_description)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
            as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}