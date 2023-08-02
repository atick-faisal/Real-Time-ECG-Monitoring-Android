package dev.atick.compose.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.base.BaseLifecycleService
import dev.atick.core.utils.extensions.collectWithLifecycle
import dev.atick.core.utils.extensions.showNotification
import dev.atick.movesense.Movesense
import dev.atick.movesense.R
import dev.atick.movesense.config.MovesenseConfig.NETWORK_UPDATE_CYCLE
import dev.atick.movesense.data.ConnectionState
import dev.atick.compose.utils.getNotificationTitle
import dev.atick.movesense.config.MovesenseConfig.NETWORK_BUFFER_LEN
import dev.atick.network.data.Ecg
import dev.atick.network.data.EcgRequest
import dev.atick.network.repository.CardiacZoneRepository
import dev.atick.network.utils.NetworkState
import dev.atick.network.utils.NetworkUtils
import dev.atick.storage.preferences.UserPreferences
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CardiacZoneService : BaseLifecycleService() {

    companion object {
        var STARTED = false
        const val PERSISTENT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.persistent"
        const val ALERT_NOTIFICATION_CHANNEL_ID = "dev.atick.c.zone.alert"
        const val BT_DEVICE_ADDRESS_KEY = "dev.atick.c.zone.device.key"
        const val NOTIFICATION_INTENT_REQUEST_CODE = 1101
        const val ALERT_NOTIFICATION_ID = 121
        const val HEART_RATE_LOW = 40
        const val HEART_RATE_HIGH = 120
        // const val HR_UPDATE_PATIENCE = 30_000L
    }

    @Inject
    lateinit var movesense: Movesense

    @Inject
    lateinit var networkUtils: NetworkUtils

    @Inject
    lateinit var cardiacZoneRepository: CardiacZoneRepository

    @Inject
    lateinit var userPreferences: UserPreferences

    private val persistentNotificationBuilder = NotificationCompat.Builder(
        this, PERSISTENT_NOTIFICATION_CHANNEL_ID
    )
    private val alertNotificationBuilder = NotificationCompat.Builder(
        this, ALERT_NOTIFICATION_CHANNEL_ID
    )

    private var notificationIntent: Intent? = null
    private var connectionStatus = ConnectionState.NOT_CONNECTED
    private var networkState = NetworkState.UNAVAILABLE

    private val ecgBuffer = MutableList(NETWORK_BUFFER_LEN) { 0 }
    private var ecgUpdateCount = 0
    private var userId = "-1"

    private var lastHrUpdateTime = 0L

    private var heartRate: Float = 80.0F

    @DrawableRes
    private var smallIcon = R.drawable.ic_alert

    @StringRes
    private var contentText = R.string.persistent_notification_not_connected_text

    override fun onCreateService() {
        super.onCreateService()
        collectWithLifecycle(movesense.heartRate) {
            heartRate = it
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

            // ... Critical heart-rate warning
            if (it < HEART_RATE_LOW || it > HEART_RATE_HIGH) {
                alertNotificationBuilder.apply {
                    setContentTitle(getString(R.string.heart_rate_warning))
                    setContentText(getString(R.string.hear_rate_warning_description, it.toInt()))
                    setSmallIcon(R.drawable.ic_alert)
                }

                if (STARTED) {
                    showNotification(
                        ALERT_NOTIFICATION_ID,
                        alertNotificationBuilder.build()
                    )
                }
            }

            // ... HR updated
            lastHrUpdateTime = System.currentTimeMillis()
        }

        collectWithLifecycle(movesense.ecgSignal) { ecgSignal ->
            ecgUpdateCount += 1
            ecgBuffer.subList(0, ecgSignal.values.size).clear()
            ecgBuffer.addAll(ecgSignal.values)

            if (ecgUpdateCount == NETWORK_UPDATE_CYCLE) {
                Logger.w("USER ID: $userId")
                Logger.i("NETWORK: $NETWORK_UPDATE_CYCLE BUFFER LENGTH: ${ecgBuffer.size}")
                val requestBody = EcgRequest(
                    patientId = userId,
                    ecg = Ecg(id = ecgSignal.timestamp, ecgData = ecgBuffer, heartRate = heartRate)
                )
                Logger.i("SENDING ECG ... ")
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val response = cardiacZoneRepository.pushEcg(requestBody)
                        Logger.i("PUSH ECG RESPONSE $response")
                    }
                }
                ecgUpdateCount = 0
            }

            // ... Auto-Kill Service
//            if (
//                lastHrUpdateTime != 0L &&
//                System.currentTimeMillis() > lastHrUpdateTime + HR_UPDATE_PATIENCE
//            ) {
//                Logger.w("STOPPING MOVESENSE SERVICE ... ")
//                stopService()
//            }
        }

        collectWithLifecycle(movesense.connectionState) { status ->
//            Logger.w("FROM SERVICE: ${status.name}")
            when (status) {
                ConnectionState.NOT_CONNECTED -> {
                    smallIcon = R.drawable.ic_alert
                    contentText = R.string.persistent_notification_not_connected_text
                    connectionStatus = ConnectionState.NOT_CONNECTED
                }
                ConnectionState.CONNECTING -> {
                    smallIcon = R.drawable.ic_connecting
                    contentText = R.string.persistent_notification_connecting_text
                    connectionStatus = ConnectionState.CONNECTING
                }
                ConnectionState.CONNECTED -> {
                    if (networkState == NetworkState.CONNECTED)
                        smallIcon = R.drawable.ic_connected
                    contentText = R.string.persistent_notification_connected_text
                    connectionStatus = ConnectionState.CONNECTED
                }
                ConnectionState.CONNECTION_FAILED -> {
                    smallIcon = R.drawable.ic_alert
                    contentText = R.string.persistent_notification_connection_failed
                    connectionStatus = ConnectionState.CONNECTION_FAILED
                }
                ConnectionState.DISCONNECTED -> {
                    smallIcon = R.drawable.ic_alert
                    contentText = R.string.persistent_notification_disconnected
                    connectionStatus = ConnectionState.DISCONNECTED
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
                    if (connectionStatus == ConnectionState.CONNECTED)
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

        collectWithLifecycle(userPreferences.getUserId()) { userId = it }
    }


    override fun onStartService(intent: Intent?) {
        val address = intent?.getStringExtra(BT_DEVICE_ADDRESS_KEY)
        // userId = intent?.getStringExtra(USER_ID_KEY) ?: "-1"
        address?.let { connect(it) }
        STARTED = true
    }

    override fun setupNotification(): Notification {
        persistentNotificationBuilder.apply {
            setSmallIcon(R.drawable.ic_warning)
            setContentTitle(
                getString(
                    R.string.movesense_disconnected
                )
            )
            setContentText(
                getString(
                    R.string.persistent_notification_text, 0.0F
                )
            )
        }
//        persistentNotificationBuilder.apply {
//            if (movesense.connectionState.value == ConnectionState.CONNECTED) {
//                setSmallIcon(R.drawable.ic_connected)
//                setContentTitle(
//                    getString(
//                        R.string.movesense_connected
//                    )
//                )
//                setContentText(
//                    getString(
//                        R.string.persistent_notification_text, 0.0F
//                    )
//                )
//            } else {
//                setSmallIcon(R.drawable.ic_warning)
//                setContentTitle(
//                    getString(
//                        R.string.persistent_notification_warning_title
//                    )
//                )
//                setContentText(
//                    getString(
//                        R.string.persistent_notification_warning_text
//                    )
//                )
//            }
//        }

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
            this@CardiacZoneService,
            NOTIFICATION_INTENT_REQUEST_CODE,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
                or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun connect(address: String) {
        movesense.initiateConnection(address)
    }

    override fun collectGarbage() {
        movesense.freeResources()
        STARTED = false
    }
}