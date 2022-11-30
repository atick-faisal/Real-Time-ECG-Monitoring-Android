package dev.atick.movesense.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.service.BaseLifecycleService
import dev.atick.core.utils.extensions.collectWithLifecycle
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.showNotification
import dev.atick.movesense.R
import dev.atick.movesense.config.MovesenseConfig.NETWORK_UPDATE_CYCLE
import dev.atick.movesense.data.ConnectionStatus
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.utils.getNotificationTitle
import dev.atick.network.data.Ecg
import dev.atick.network.data.EcgRequest
import dev.atick.network.data.RPeak
import dev.atick.network.repository.CardiacZoneRepository
import dev.atick.network.utils.NetworkState
import dev.atick.network.utils.NetworkUtils
import javax.inject.Inject


@AndroidEntryPoint
class MovesenseService : BaseLifecycleService() {

    companion object {
        var STARTED = false
        const val PERSISTENT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.persistent"
        const val ALERT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.alert"
        const val BT_DEVICE_ADDRESS_KEY = "dev.atick.c.zone.device.key"
        const val USER_ID_KEY = "dev.atick.c.zone.user.id"
        const val NOTIFICATION_INTENT_REQUEST_CODE = 1101
        const val ALERT_NOTIFICATION_ID = 121
    }

    @Inject
    lateinit var movesense: Movesense

    @Inject
    lateinit var networkUtils: NetworkUtils

    @Inject
    lateinit var cardiacZoneRepository: CardiacZoneRepository

    private val persistentNotificationBuilder = NotificationCompat.Builder(
        this, PERSISTENT_NOTIFICATION_CHANNEL_ID
    )
    private val alertNotificationBuilder = NotificationCompat.Builder(
        this, ALERT_NOTIFICATION_CHANNEL_ID
    )

    private var notificationIntent: Intent? = null
    private var connectionStatus = ConnectionStatus.NOT_CONNECTED
    private var networkState = NetworkState.UNAVAILABLE

    private var ecgUpdateCount = 0
    private var userId = "-1"

    @DrawableRes
    private var smallIcon = R.drawable.ic_alert

    @StringRes
    private var contentText = R.string.persistent_notification_not_connected_text

    override fun onCreateService() {
        super.onCreateService()
        observe(movesense.averageHeartRate) {
            persistentNotificationBuilder.apply {
                setContentTitle(
                    getNotificationTitle(
                        connectionStatus = connectionStatus,
                        networkState = networkState
                    )
                )
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

        observe(movesense.ecg) { ecg ->
            ecgUpdateCount += 1
            if (ecgUpdateCount == NETWORK_UPDATE_CYCLE) {
                Logger.w("USER ID: $userId")
                val requestBody = EcgRequest(
                    patientId = userId,
                    ecg = Ecg(
                        ecgData = ecg.signal,
                        rPeaks = ecg.rPeaks
                            .filter { rPeak ->
                                rPeak.location < ecg.signal.size
                            }
                            .map { rPeak ->
                                RPeak(
                                    x = rPeak.location,
                                    y = rPeak.amplitude
                                )
                            }
                    )
                )
                Logger.i("SENDING ECG ... ")
                lifecycleScope.launchWhenStarted {
                    cardiacZoneRepository.pushEcg(requestBody)
                }
                ecgUpdateCount = 0
            }
        }

        observe(movesense.connectionStatus) { status ->
            when (status) {
                ConnectionStatus.NOT_CONNECTED -> {
                    smallIcon = R.drawable.ic_alert
                    contentText = R.string.persistent_notification_not_connected_text
                    connectionStatus = ConnectionStatus.NOT_CONNECTED
                }
                ConnectionStatus.CONNECTING -> {
                    smallIcon = R.drawable.ic_connecting
                    contentText = R.string.persistent_notification_connecting_text
                    connectionStatus = ConnectionStatus.CONNECTING
                }
                ConnectionStatus.CONNECTED -> {
                    if (networkState == NetworkState.CONNECTED)
                        smallIcon = R.drawable.ic_connected
                    contentText = R.string.persistent_notification_connected_text
                    connectionStatus = ConnectionStatus.CONNECTED
                }
                ConnectionStatus.CONNECTION_FAILED -> {
                    smallIcon = R.drawable.ic_alert
                    contentText = R.string.persistent_notification_connection_failed
                    connectionStatus = ConnectionStatus.CONNECTION_FAILED
                }
                ConnectionStatus.DISCONNECTED -> {
                    smallIcon = R.drawable.ic_alert
                    contentText = R.string.persistent_notification_disconnected
                    connectionStatus = ConnectionStatus.DISCONNECTED
                }
            }

            persistentNotificationBuilder.apply {
                setContentTitle(getString(status.description))
                setContentText(getString(contentText))
                setSmallIcon(smallIcon)
            }

            alertNotificationBuilder.apply {
                setContentTitle(getString(status.description))
                setContentText(getString(contentText))
                setSmallIcon(smallIcon)
            }

            if (STARTED) {
                showNotification(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotificationBuilder.build()
                )

                showNotification(
                    ALERT_NOTIFICATION_ID,
                    alertNotificationBuilder.build()
                )
            }
        }

        collectWithLifecycle(networkUtils.currentState) { state ->
            when (state) {
                NetworkState.CONNECTED -> {
                    networkState = NetworkState.CONNECTED
                    if (connectionStatus == ConnectionStatus.CONNECTED)
                        smallIcon = R.drawable.ic_connected
                    contentText = R.string.network_established
                }
                NetworkState.LOSING -> {
                    networkState = NetworkState.LOSING
                    smallIcon = R.drawable.ic_connecting
                    contentText = R.string.network_unavailable
                }
                NetworkState.LOST -> {
                    networkState = NetworkState.LOST
                    smallIcon = R.drawable.ic_no_internet
                    contentText = R.string.network_unavailable
                }
                NetworkState.UNAVAILABLE -> {
                    networkState = NetworkState.UNAVAILABLE
                    smallIcon = R.drawable.ic_no_internet
                    contentText = R.string.network_unavailable
                }
            }

            persistentNotificationBuilder.apply {
                setContentTitle(
                    getNotificationTitle(
                        connectionStatus = connectionStatus,
                        networkState = networkState
                    )
                )
                setContentText(getString(contentText))
                setSmallIcon(smallIcon)
            }

            alertNotificationBuilder.apply {
                setContentTitle(getString(state.description))
                setContentText(getString(contentText))
                setSmallIcon(smallIcon)
            }

            if (STARTED) {
                showNotification(
                    PERSISTENT_NOTIFICATION_ID,
                    persistentNotificationBuilder.build()
                )

                showNotification(
                    ALERT_NOTIFICATION_ID,
                    alertNotificationBuilder.build()
                )
            }
        }
    }


    override fun onStartService(intent: Intent?) {
        val address = intent?.getStringExtra(BT_DEVICE_ADDRESS_KEY)
        userId = intent?.getStringExtra(USER_ID_KEY) ?: "-1"
        address?.let { connect(it) }
        STARTED = true
    }

    override fun setupNotification(): Notification {
        persistentNotificationBuilder.apply {
            if (movesense.isConnected.value == true) {
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
            } else {
                setSmallIcon(R.drawable.ic_warning)
                setContentTitle(
                    getString(
                        R.string.persistent_notification_warning_title
                    )
                )
                setContentText(
                    getString(
                        R.string.persistent_notification_warning_text
                    )
                )
            }
        }

        val pendingIntent = getMainActivityPendingIntent()

        persistentNotificationBuilder.apply {
            priority = NotificationCompat.PRIORITY_LOW
            pendingIntent?.let { setContentIntent(it) }
        }

        alertNotificationBuilder.apply {
            priority = NotificationCompat.PRIORITY_HIGH
            pendingIntent?.let { setContentIntent(pendingIntent) }
        }

        return persistentNotificationBuilder.build()
    }

    private fun getMainActivityPendingIntent(): PendingIntent? {
        try {
            notificationIntent = Intent(
                this,
                Class.forName("dev.atick.compose.ui.MainActivity")
            )
        } catch (e: ClassNotFoundException) {
            Logger.e("MAIN ACTIVITY NOT FOUND!")
            e.printStackTrace()
        }

        return PendingIntent.getActivity(
            this@MovesenseService,
            NOTIFICATION_INTENT_REQUEST_CODE,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
                or PendingIntent.FLAG_UPDATE_CURRENT
        )
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