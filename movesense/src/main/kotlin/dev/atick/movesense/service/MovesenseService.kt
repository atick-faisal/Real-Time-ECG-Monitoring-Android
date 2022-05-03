package dev.atick.movesense.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.service.BaseLifecycleService
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.showNotification
import dev.atick.movesense.R
import dev.atick.movesense.data.ConnectionStatus
import dev.atick.movesense.repository.Movesense
import javax.inject.Inject

@AndroidEntryPoint
class MovesenseService : BaseLifecycleService() {

    companion object {
        var STARTED = false
        const val PERSISTENT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.persistent"

        // const val ALERT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.alert"
        // const val ALERT_NOTIFICATION_ID = 1011
        const val BT_DEVICE_ADDRESS_KEY = "dev.atick.c.zone.device.key"
        const val PERSISTENT_NOTIFICATION_REQUEST_CODE = 1101
    }

    @Inject
    lateinit var movesense: Movesense

    private val persistentNotificationBuilder = NotificationCompat.Builder(
        this, PERSISTENT_NOTIFICATION_CHANNEL_ID
    )

    private var notificationIntent: Intent? = null

    override fun onCreateService() {
        super.onCreateService()
        observe(movesense.averageHeartRate) {
            persistentNotificationBuilder.setContentText(
                getString(R.string.persistent_notification_text, it),
            )
            if (STARTED) {
                showNotification(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotificationBuilder.build()
                )
            }
        }
        observe(movesense.connectionStatus) { connectionStatus ->
            persistentNotificationBuilder.apply {
                when (connectionStatus) {
                    ConnectionStatus.NOT_CONNECTED -> {
                        setSmallIcon(R.drawable.ic_warning)
                        setContentTitle(getString(R.string.not_connected))
                        setContentText(
                            getString(
                                R.string.persistent_notification_not_connected_text
                            )
                        )
                    }
                    ConnectionStatus.CONNECTING -> {
                        setSmallIcon(R.drawable.ic_connecting)
                        setContentTitle(getString(R.string.connecting))
                        setContentText(
                            getString(
                                R.string.persistent_notification_connecting_text
                            )
                        )
                    }
                    ConnectionStatus.CONNECTED -> {
                        setSmallIcon(R.drawable.ic_connected)
                        setContentTitle(
                            getString(
                                R.string.movesense_connected
                            )
                        )
                        setContentText(
                            getString(
                                R.string.persistent_notification_text, 0.0F
                            )
                        )
                    }
                    ConnectionStatus.CONNECTION_FAILED -> {
                        setSmallIcon(R.drawable.ic_connection_failed)
                        setContentTitle(
                            getString(
                                R.string.connection_failed
                            )
                        )
                        setContentText(
                            getString(
                                R.string.persistent_notification_connection_failed
                            )
                        )
                    }
                    ConnectionStatus.DISCONNECTED -> {
                        setSmallIcon(R.drawable.ic_warning)
                        setContentTitle(
                            getString(
                                R.string.disconnected
                            )
                        )
                        setContentText(
                            getString(
                                R.string.persistent_notification_disconnected
                            )
                        )
                    }
                }
            }

            if (STARTED) {
                showNotification(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotificationBuilder.build()
                )
            }
        }
    }

    override fun onStartService(intent: Intent?) {
        val address = intent?.getStringExtra(BT_DEVICE_ADDRESS_KEY)
        address?.let { connect(it) }
        STARTED = true
    }

    override fun setupNotification(): Notification {
        try {
            notificationIntent = Intent(
                this,
                Class.forName("dev.atick.compose.ui.MainActivity")
            )
        } catch (e: ClassNotFoundException) {
            Logger.e("MAIN ACTIVITY NOT FOUND!")
            e.printStackTrace()
        }
        val notification = if (movesense.isConnected.value == true) {
            persistentNotificationBuilder
                .setSmallIcon(R.drawable.ic_connected)
                .setContentTitle(
                    getString(
                        R.string.connected
                    )
                )
                .setContentText(
                    getString(
                        R.string.persistent_notification_text, 0.0F
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_LOW)
        } else {
            persistentNotificationBuilder
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle(
                    getString(
                        R.string.persistent_notification_warning_title
                    )
                )
                .setContentText(
                    getString(
                        R.string.persistent_notification_warning_text
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_LOW)
        }

        notificationIntent?.let {
            notification.apply {
                val pendingIntent = PendingIntent.getActivity(
                    this@MovesenseService,
                    PERSISTENT_NOTIFICATION_REQUEST_CODE,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                        or PendingIntent.FLAG_UPDATE_CURRENT
                )
                setContentIntent(pendingIntent)
            }
        }

        return notification.build()
    }

    private fun connect(address: String) {
        movesense.connect(address) {
            movesense.stopScan()
        }
    }

    override fun collectGarbage() {
        movesense.clear()
        STARTED = false
    }
}