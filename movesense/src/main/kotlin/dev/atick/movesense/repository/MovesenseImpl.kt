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
import dev.atick.core.utils.Event
import dev.atick.movesense.data.BtDevice
import dev.atick.movesense.data.EcgInfoResponse
import dev.atick.movesense.data.HrResponse
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@SuppressLint("MissingPermission")
class MovesenseImpl @Inject constructor(
    private val mds: Mds?,
    private val rxBleClient: RxBleClient?
) : Movesense {

    companion object {
        const val URI_EVENT_LISTENER = "suunto://MDS/EventListener"
        const val SCHEME_PREFIX = "suunto://"

        const val URI_ECG_INFO = "/Meas/ECG/Info"
        const val URI_ECG_ROOT = "/Meas/ECG/"
        const val URI_MEAS_HR = "/Meas/HR"
    }

    private var scanDisposable: Disposable? = null
    private var hrSubscription: MdsSubscription? = null

    private val _connectionStatus = MutableLiveData<Event<String?>>(
        Event(null)
    )
    override val connectionStatus: LiveData<Event<String?>>
        get() = _connectionStatus

    private val _averageHeartRate = MutableLiveData(0.0F)
    override val averageHeartRate: LiveData<Float>
        get() = _averageHeartRate

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
                    Logger.w("DEVICE FOUND: $result")
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

    override fun stopScan() {
        Logger.w("STOPPING SCAN")
        scanDisposable?.dispose()
        scanDisposable = null
    }

    override fun connect(address: String, onConnect: () -> Unit) {
        mds?.connect(address, object : MdsConnectionListener {
            override fun onConnect(address: String?) {
                _connectionStatus.postValue(Event("Connecting ... "))
                Logger.i("CONNECTION TO $address")
            }

            override fun onConnectionComplete(address: String?, serial: String?) {
                _connectionStatus.postValue(Event("Connected"))
                Logger.i("CONNECTED TO: $address")
                fetchEcgInfo(serial)
                onConnect.invoke()
            }

            override fun onError(e: MdsException?) {
                _connectionStatus.postValue(Event("Connection Error"))
                Logger.e("CONNECTION ERROR: $e")
            }

            override fun onDisconnect(address: String?) {
                _connectionStatus.postValue(Event("Disconnected"))
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
                    Logger.w("ECG INFO: $ecgInfo")
                    subscribeToHRService(connectedSerial)
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
                            Logger.i("RR: $it")
                        }
                        Logger.i("HR: ${hrResponse?.body?.rrData}")
                    } catch (e: JsonSyntaxException) {
                        Logger.e("HR PARSING ERROR: $e")
                    }
                }

                override fun onError(e: MdsException?) {
                    Logger.e("HR SUBSCRIPTION ERROR: $e")
                }

            })
    }

    private fun unsubscribeHr() {
        hrSubscription?.unsubscribe()
        hrSubscription = null
    }
}