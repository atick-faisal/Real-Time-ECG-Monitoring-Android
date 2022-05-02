package dev.atick.movesense.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.orhanobut.logger.Logger
import dev.atick.core.service.BaseService
import dev.atick.core.utils.extensions.observe
import dev.atick.movesense.R
import dev.atick.movesense.repository.Movesense
import javax.inject.Inject

class MovesenseService : BaseService() {

    companion object {
        var STARTED = false
        const val PERSISTENT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.persistent"
        // const val ALERT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.alert"
        // const val ALERT_NOTIFICATION_ID = 1011
    }

    @Inject
    lateinit var movesense: Movesense

    private val persistentNotificationBuilder = NotificationCompat.Builder(
        this, PERSISTENT_NOTIFICATION_CHANNEL_ID
    )

    private var notificationIntent: Intent? = null

    override fun onStartService(intent: Intent?) {
        STARTED = true
        observe(movesense.averageHeartRate) {

        }
    }

    override fun setupNotification(): Notification {
        try {
            notificationIntent = Intent(
                this, Class.forName("dev.atick.compose.MainActivity")
            )
        } catch (e: ClassNotFoundException) {
            Logger.i("MAIN ACTIVITY NOT FOUND!")
            e.printStackTrace()
        }
        val notification = if (movesense.isConnected.value == true) {
            persistentNotificationBuilder
                .setSmallIcon(R.drawable.ic_connected)
                .setContentTitle(getString(R.string.persistent_notification_title))
                // .setContentText(getString(R.string.persistent_notification_description))
                .setPriority(NotificationCompat.PRIORITY_LOW)
        } else {
            persistentNotificationBuilder
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle(getString(R.string.persistent_notification_warning_title))
                // .setContentText(getString(R.string.persistent_notification_warning_description))
                .setPriority(NotificationCompat.PRIORITY_LOW)
        }

        notificationIntent?.let {
            notification.apply {
                val pendingIntent = PendingIntent.getActivity(
                    this@MovesenseService,
                    1011,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                setContentIntent(pendingIntent)
            }
        }

        return notification.build()
    }

    override fun collectGarbage() {
        movesense.clear()
        STARTED = false
    }
}