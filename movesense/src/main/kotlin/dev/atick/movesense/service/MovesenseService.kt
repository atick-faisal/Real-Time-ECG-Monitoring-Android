package dev.atick.movesense.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.service.BaseLifecycleService
import dev.atick.core.utils.extensions.collectWithLifecycle
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.showNotification
import dev.atick.movesense.R
import dev.atick.movesense.data.ConnectionStatus
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.utils.getNotificationTitle
import dev.atick.network.utils.NetworkState
import dev.atick.network.utils.NetworkUtils
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

    @Inject
    lateinit var networkUtils: NetworkUtils

    private val persistentNotificationBuilder = NotificationCompat.Builder(
        this, PERSISTENT_NOTIFICATION_CHANNEL_ID
    )

    private var notificationIntent: Intent? = null
    private var connectionStatus = ConnectionStatus.NOT_CONNECTED
    private var networkState = NetworkState.UNAVAILABLE

    override fun onCreateService() {
        super.onCreateService()
        observe(movesense.averageHeartRate) {
            persistentNotificationBuilder.apply {
                setContentText(
                    getString(
                        R.string.persistent_notification_text, it
                    )
                )
            }
            if (STARTED) {
                showNotification(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotificationBuilder.build()
                )
            }
        }

        observe(movesense.connectionStatus) { status ->
            persistentNotificationBuilder.apply {
                when (status) {
                    ConnectionStatus.NOT_CONNECTED -> {
                        connectionStatus = ConnectionStatus.NOT_CONNECTED
                        setSmallIcon(R.drawable.ic_alert)
                        setContentText(
                            getString(
                                R.string.persistent_notification_not_connected_text
                            )
                        )
                    }
                    ConnectionStatus.CONNECTING -> {
                        setSmallIcon(R.drawable.ic_connecting)
                        setContentText(
                            getString(
                                R.string.persistent_notification_connecting_text
                            )
                        )
                        connectionStatus = ConnectionStatus.CONNECTING
                    }
                    ConnectionStatus.CONNECTED -> {
                        if (networkState == NetworkState.CONNECTED)
                            setSmallIcon(R.drawable.ic_connected)
                        setContentText(
                            getString(
                                R.string.persistent_notification_text, 0.0F
                            )
                        )
                        connectionStatus = ConnectionStatus.CONNECTED
                    }
                    ConnectionStatus.CONNECTION_FAILED -> {
                        setSmallIcon(R.drawable.ic_alert)
                        setContentText(
                            getString(
                                R.string.persistent_notification_connection_failed
                            )
                        )
                        connectionStatus = ConnectionStatus.CONNECTION_FAILED
                    }
                    ConnectionStatus.DISCONNECTED -> {
                        setSmallIcon(R.drawable.ic_alert)
                        setContentText(
                            getString(
                                R.string.persistent_notification_disconnected
                            )
                        )
                        connectionStatus = ConnectionStatus.DISCONNECTED
                    }
                }

                setContentTitle(
                    getNotificationTitle(
                        connectionStatus = connectionStatus,
                        networkState = networkState
                    )
                )
            }

            if (STARTED) {
                showNotification(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotificationBuilder.build()
                )
            }
        }

        collectWithLifecycle(networkUtils.currentState) { state ->
            persistentNotificationBuilder.apply {
                when (state) {
                    NetworkState.CONNECTED -> {
                        networkState = NetworkState.CONNECTED
                        if (connectionStatus == ConnectionStatus.CONNECTED)
                            setSmallIcon(R.drawable.ic_connected)
                    }
                    NetworkState.LOSING -> {
                        networkState = NetworkState.LOSING
                        setSmallIcon(R.drawable.ic_connecting)
                    }
                    NetworkState.LOST -> {
                        networkState = NetworkState.LOST
                        setSmallIcon(R.drawable.ic_no_internet)
                    }
                    NetworkState.UNAVAILABLE -> {
                        networkState = NetworkState.UNAVAILABLE
                        setSmallIcon(R.drawable.ic_no_internet)
                    }
                }

                setContentTitle(
                    getNotificationTitle(
                        connectionStatus = connectionStatus,
                        networkState = networkState
                    )
                )
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
                priority = NotificationCompat.PRIORITY_DEFAULT
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