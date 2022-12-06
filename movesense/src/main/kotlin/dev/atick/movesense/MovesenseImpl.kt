package dev.atick.movesense

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.movesense.mds.*
import com.orhanobut.logger.Logger
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanSettings
import dev.atick.movesense.config.MovesenseConfig
import dev.atick.movesense.data.ConnectionState
import dev.atick.movesense.data.EcgResponse
import dev.atick.movesense.data.EcgSignal
import dev.atick.movesense.data.HrResponse
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MovesenseImpl @Inject constructor(
    private val mds: Mds?,
    private val rxBleClient: RxBleClient?
) : Movesense {

    private var connectedMac: String? = null
    private var scanDisposable: Disposable? = null
    private var hrSubscription: MdsSubscription? = null
    private var ecgSubscription: MdsSubscription? = null

    private val _connectionState = MutableStateFlow(ConnectionState.NOT_CONNECTED)
    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState

    private val _heartRate = MutableSharedFlow<Float>()
    override val heartRate: SharedFlow<Float>
        get() = _heartRate

    private val _ecgSignal = MutableSharedFlow<EcgSignal>()
    override val ecgSignal: SharedFlow<EcgSignal>
        get() = _ecgSignal

    private val connectionListener = object : MdsConnectionListener {
        override fun onConnect(address: String?) {
            Logger.w("CONNECTING TO $address")
            CoroutineScope(Dispatchers.Default).launch {
                _connectionState.emit(ConnectionState.CONNECTING)
            }
        }

        override fun onConnectionComplete(address: String?, serial: String?) {
            connectedMac = address
            Logger.i("CONNECTED TO: $address")
            CoroutineScope(Dispatchers.Default).launch {
                _connectionState.emit(ConnectionState.CONNECTED)
            }
            serial?.let { connectedSerial ->
                subscribeToHrService(connectedSerial)
                subscribeToEcgService(connectedSerial)
            }
        }

        override fun onError(e: MdsException?) {
            Logger.e("CONNECTION ERROR: $e")
            CoroutineScope(Dispatchers.Default).launch {
                _connectionState.emit(ConnectionState.CONNECTION_FAILED)
            }
        }

        override fun onDisconnect(address: String?) {
            Logger.e("DISCONNECTED FROM $address")
            CoroutineScope(Dispatchers.Default).launch {
                _connectionState.emit(ConnectionState.DISCONNECTED)
            }
        }
    }

    private val hrNotificationListener = object : MdsNotificationListener {
        override fun onNotification(data: String?) {
            try {
                val hrResponse = Gson().fromJson(
                    data, HrResponse::class.java
                )
                hrResponse?.body?.average?.let { heartRate ->
                    // Logger.i("HR: $heartRate")
                    CoroutineScope(Dispatchers.Default).launch {
                        _heartRate.emit(heartRate)
                    }
                }
            } catch (e: JsonSyntaxException) {
                Logger.e("HR PARSING ERROR: $e")
            } catch (e: ArrayIndexOutOfBoundsException) {
                Logger.e("RR INTERVAL ERROR: $e")
            }
        }

        override fun onError(e: MdsException?) {
            Logger.e("HR SUBSCRIPTION ERROR: $e")
        }
    }

    private val ecgNotificationListener = object : MdsNotificationListener {
        override fun onNotification(data: String?) {
            try {
                val ecgResponse = Gson().fromJson(
                    data, EcgResponse::class.java
                )
                ecgResponse?.body?.samples?.let { ecgSamples ->
                    // Logger.i("ECG: $ecgSamples")
                    CoroutineScope(Dispatchers.Default).launch {
                        _ecgSignal.emit(EcgSignal(ecgSamples))
                    }
                }
            } catch (e: JsonSyntaxException) {
                Logger.e("ECG PARSING ERROR: $e")
            }
        }

        override fun onError(e: MdsException?) {
            Logger.e("ECG SUBSCRIPTION ERROR: $e")
        }
    }

    override suspend fun getMovesenseDeviceAddress(): String? {
        return suspendCoroutine { continuation ->
            Logger.i("SCANNING ... ")

            // ... Auto Stop Scan
            val cancellationHandler = Handler(Looper.getMainLooper())
            cancellationHandler.postDelayed({
                try {
                    stopScan()
                    continuation.resume(null)
                } catch (e: java.lang.IllegalStateException) {
                    Logger.e("ALREADY RESUMED ERROR")
                }
            }, MovesenseConfig.SCAN_TIMEOUT)

            scanDisposable = rxBleClient?.scanBleDevices(
                ScanSettings.Builder()
                    // .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    // .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .build()
            )?.subscribe(
                { scanResult ->
                    scanResult?.bleDevice?.let { device ->
                        if (device.name?.contains("Movesense") == true) {
                            Logger.i("DEVICE FOUND: ${device.name}")
                            cancellationHandler.removeCallbacksAndMessages(null)
                            continuation.resume(device.macAddress)
                            stopScan()
                        }
                    }
                },
                { throwable ->
                    continuation.resume(null)
                    Logger.e("SCAN ERROR: $throwable")
                }
            )
        }
    }

    override fun initiateConnection(address: String) {
        mds?.connect(address, connectionListener)
    }

    override fun freeResources() {
        stopScan()
        unsubscribeHr()
        unsubscribeEcg()
        disconnect()
    }

    private fun stopScan() {
        Logger.w("STOPPING SCAN ... ")
        scanDisposable?.dispose()
        scanDisposable = null
    }

    private fun subscribeToHrService(connectedSerial: String) {
        val sb = StringBuilder()
        val contract = sb
            .append("{\"Uri\": \"")
            .append(connectedSerial)
            .append(MovesenseConfig.URI_MEAS_HR)
            .append("\"}")
            .toString()

        Logger.i("HR CONTRACT: $contract")

        unsubscribeHr()
        hrSubscription = mds?.subscribe(
            MovesenseConfig.URI_EVENT_LISTENER,
            contract,
            hrNotificationListener
        )
    }

    private fun subscribeToEcgService(connectedSerial: String) {
        val sb = StringBuilder()
        val contract = sb
            .append("{\"Uri\": \"")
            .append(connectedSerial)
            .append(MovesenseConfig.URI_ECG_ROOT)
            .append(MovesenseConfig.DEFAULT_ECG_SAMPLE_RATE)
            .append("\"}")
            .toString()

        Logger.i("HR CONTRACT: $contract")

        unsubscribeEcg()
        ecgSubscription = mds?.subscribe(
            MovesenseConfig.URI_EVENT_LISTENER,
            contract,
            ecgNotificationListener
        )
    }

    private fun unsubscribeHr() {
        Logger.w("UNSUBSCRIBING HR ... ")
        hrSubscription?.unsubscribe()
        hrSubscription = null
    }

    private fun unsubscribeEcg() {
        Logger.w("UNSUBSCRIBING ECG ... ")
        ecgSubscription?.unsubscribe()
        ecgSubscription = null
    }

    private fun disconnect() {
        Logger.w("DISCONNECTING ... ")
        connectedMac?.let { macAddress ->
            mds?.disconnect(macAddress)
            connectedMac = null
        }
    }
}