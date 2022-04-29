package dev.atick.movesense.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import com.movesense.mds.Mds
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.orhanobut.logger.Logger
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanSettings
import dev.atick.movesense.data.BtDevice
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@SuppressLint("MissingPermission")
class MovesenseImpl @Inject constructor(
    private val mds: Mds?,
    private val rxBleClient: RxBleClient?
) : Movesense {

    private var scanDisposable: Disposable? = null

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
            override fun onConnect(p0: String?) {
                Logger.i("ON CONNECT!")
            }

            override fun onConnectionComplete(p0: String?, p1: String?) {
                Logger.i("CONNECTION SUCCESSFUL")
                onConnect.invoke()
            }

            override fun onError(p0: MdsException?) {
                Logger.e("CONNECTION FAILED!")
            }

            override fun onDisconnect(p0: String?) {
                Logger.w("DISCONNECTED!")
            }
        })
    }
}