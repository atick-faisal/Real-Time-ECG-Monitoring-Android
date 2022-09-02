package dev.atick.movesense.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.movesense.mds.*
import com.orhanobut.logger.Logger
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanSettings
import dev.atick.core.utils.extensions.argmax
import dev.atick.movesense.config.MovesenseConfig.DEFAULT_ECG_BUFFER_LEN
import dev.atick.movesense.config.MovesenseConfig.DEFAULT_ECG_SAMPLE_RATE
import dev.atick.movesense.config.MovesenseConfig.ECG_SEGMENT_LEN
import dev.atick.movesense.config.MovesenseConfig.SCHEME_PREFIX
import dev.atick.movesense.config.MovesenseConfig.URI_ECG_INFO
import dev.atick.movesense.config.MovesenseConfig.URI_ECG_ROOT
import dev.atick.movesense.config.MovesenseConfig.URI_EVENT_LISTENER
import dev.atick.movesense.config.MovesenseConfig.URI_MEAS_HR
import dev.atick.movesense.data.*
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@SuppressLint("MissingPermission")
class MovesenseImpl @Inject constructor(
    private val mds: Mds?,
    private val rxBleClient: RxBleClient?
) : Movesense {
    private var rPeakFlag = false
    private var previousEcgBuffer = MutableList(DEFAULT_ECG_BUFFER_LEN * 5) { 0 }

    private var connectedMac: String? = null
    private var scanDisposable: Disposable? = null
    private var hrSubscription: MdsSubscription? = null
    private var ecgSubscription: MdsSubscription? = null

    private var bufferLen: Int = DEFAULT_ECG_BUFFER_LEN
    private val ecgBuffer = MutableList(ECG_SEGMENT_LEN) { 0 }
    private var rPeakBuffer = mutableListOf<RPeakData>()

    private val _isConnected = MutableLiveData(false)
    override val isConnected: LiveData<Boolean>
        get() = _isConnected

    private val _connectionStatus = MutableLiveData(ConnectionStatus.NOT_CONNECTED)
    override val connectionStatus: LiveData<ConnectionStatus>
        get() = _connectionStatus

    private val _averageHeartRate = MutableLiveData(0.0F)
    override val averageHeartRate: LiveData<Float>
        get() = _averageHeartRate

    private val _rrInterval = MutableLiveData(0)
    override val rrInterval: LiveData<Int>
        get() = _rrInterval

    private val _ecgData = MutableLiveData<List<Int>>(ecgBuffer)
    override val ecgData: LiveData<List<Int>>
        get() = _ecgData

    private val _rPeakData = MutableLiveData<List<RPeakData>>()
    override val rPeakData: LiveData<List<RPeakData>>
        get() = _rPeakData

    override fun startScan(onDeviceFound: (BtDevice) -> Unit) {
        Logger.i("SCANNING ... ")
        scanDisposable = rxBleClient?.scanBleDevices(
            ScanSettings.Builder()
                // .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                // .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build()
        )?.subscribe(
            { scanResult ->
                scanResult?.let { result ->
                    Logger.i("DEVICE FOUND: $result")
                    onDeviceFound(
                        BtDevice(
                            name = result.bleDevice.name ?: "Unnamed",
                            address = result.bleDevice?.macAddress ?: "Unknown",
                            rssi = result.rssi,
                            type = result.bleDevice?.bluetoothDevice
                                ?.bluetoothClass?.majorDeviceClass
                                ?: BluetoothClass.Device.Major.COMPUTER
                        )
                    )
                }
            },
            { throwable ->
                Logger.e("SCAN ERROR: $throwable")
            }
        )
    }

    override fun connect(address: String, onConnect: () -> Unit) {
        mds?.connect(address, object : MdsConnectionListener {
            override fun onConnect(address: String?) {
                _connectionStatus.postValue(ConnectionStatus.CONNECTING)
                Logger.i("CONNECTION TO $address")
            }

            override fun onConnectionComplete(address: String?, serial: String?) {
                connectedMac = address
                _isConnected.postValue(true)
                _connectionStatus.postValue(ConnectionStatus.CONNECTED)
                Logger.i("CONNECTED TO: $address")
                fetchEcgInfo(serial)
                onConnect.invoke()
            }

            override fun onError(e: MdsException?) {
                _isConnected.postValue(false)
                _connectionStatus.postValue(ConnectionStatus.CONNECTION_FAILED)
                Logger.e("CONNECTION ERROR: $e")
            }

            override fun onDisconnect(address: String?) {
                _isConnected.postValue(false)
                _connectionStatus.postValue(ConnectionStatus.DISCONNECTED)
                Logger.w("DISCONNECTED FROM $address")
            }
        })
    }

    private fun fetchEcgInfo(connectedSerial: String?) {
        val uri = SCHEME_PREFIX + connectedSerial + URI_ECG_INFO
        mds?.get(uri, null, object : MdsResponseListener {
            override fun onSuccess(data: String?, header: MdsHeader?) {
                try {
                    val ecgInfo = Gson().fromJson(
                        data, EcgInfoResponse::class.java
                    )
                    bufferLen = ecgInfo?.content?.arraySize
                        ?: DEFAULT_ECG_BUFFER_LEN
                    subscribeToHRService(connectedSerial)
                    subscribeToEcgService(connectedSerial)
                    Logger.w("ECG INFO: $ecgInfo")
                } catch (e: JsonSyntaxException) {
                    Logger.e("ECG INFO PARSING ERROR: $e")
                }
                super.onSuccess(data, header)
            }

            override fun onError(e: MdsException?) {
                Logger.e("ECG INFO READ ERROR: $e")
            }
        })
    }

    private fun subscribeToHRService(connectedSerial: String?) {
        val sb = StringBuilder()
        val contract = sb
            .append("{\"Uri\": \"")
            .append(connectedSerial)
            .append(URI_MEAS_HR)
            .append("\"}")
            .toString()

        Logger.i("HR CONTRACT: $contract")

        unsubscribeHr()
        hrSubscription = mds?.subscribe(
            URI_EVENT_LISTENER, contract, object : MdsNotificationListener {
                override fun onNotification(data: String?) {
                    try {
                        val hrResponse = Gson().fromJson(
                            data, HrResponse::class.java
                        )
                        hrResponse?.body?.average?.let {
                            _averageHeartRate.postValue(it)
                            // Logger.i("RR: $it")
                        }
                        hrResponse?.body?.let { body ->
                            body.average.let { hr ->
                                _averageHeartRate.postValue(hr)

                                // ... R-peak is present in the second previous ECG buffer
                                // ... Adjust R-peak location for ECG_SEGMENT_LEN
                                val rPeakLocation = previousEcgBuffer.argmax() +
                                    ECG_SEGMENT_LEN - 5 * DEFAULT_ECG_BUFFER_LEN
                                rPeakBuffer.add(
                                    RPeakData(
                                        rPeakLocation,
                                        previousEcgBuffer.maxOrNull() ?: 0
                                    )
                                )
                                // Logger.w("$rPeakBuffer")
                            }
                            body.rrData.let { rrIntervals ->
                                _rrInterval.postValue(rrIntervals[0])
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

            })
    }

    private fun subscribeToEcgService(connectedSerial: String?) {
        val sb = StringBuilder()
        val contract = sb
            .append("{\"Uri\": \"")
            .append(connectedSerial)
            .append(URI_ECG_ROOT)
            .append(DEFAULT_ECG_SAMPLE_RATE)
            .append("\"}")
            .toString()

        Logger.i("HR CONTRACT: $contract")

        unsubscribeEcg()
        ecgSubscription = mds?.subscribe(
            URI_EVENT_LISTENER, contract, object : MdsNotificationListener {
                override fun onNotification(data: String?) {
                    try {
                        val ecgResponse = Gson().fromJson(
                            data, EcgResponse::class.java
                        )
                        ecgResponse?.body?.samples?.let { ecgSamples ->
                            ecgBuffer.subList(0, bufferLen).clear()
                            ecgBuffer.addAll(ecgSamples)
                            _ecgData.postValue(ecgBuffer)

                            if (rPeakFlag) {
                                Logger.w("$previousEcgBuffer PEAK: ${previousEcgBuffer.maxOrNull()}")
                                rPeakFlag = false
                            }

                            previousEcgBuffer.subList(0, bufferLen).clear()
                            previousEcgBuffer.addAll(ecgSamples)
                            rPeakBuffer =
                                rPeakBuffer.map {
                                    RPeakData(
                                        it.location - DEFAULT_ECG_BUFFER_LEN,
                                        it.amplitude
                                    )
                                }.toMutableList()
                            rPeakBuffer = rPeakBuffer.filter { it.location >= 0 }.toMutableList()
                            _rPeakData.postValue(rPeakBuffer)

                            // Logger.i("ECG: $it")
                        }
                    } catch (e: JsonSyntaxException) {
                        Logger.e("ECG PARSING ERROR: $e")
                    }
                }

                override fun onError(e: MdsException?) {
                    Logger.e("ECG SUBSCRIPTION ERROR: $e")
                }

            }
        )
    }

    private fun disconnect() {
        Logger.w("DISCONNECTING ... ")
        connectedMac?.let {
            mds?.disconnect(it)
            connectedMac = null
            _connectionStatus.postValue(ConnectionStatus.DISCONNECTED)
            _isConnected.postValue(false)
        }
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

    override fun stopScan() {
        Logger.w("STOPPING SCAN ... ")
        scanDisposable?.dispose()
        scanDisposable = null
    }

    override fun clear() {
        stopScan()
        unsubscribeHr()
        unsubscribeEcg()
        disconnect()
    }
}