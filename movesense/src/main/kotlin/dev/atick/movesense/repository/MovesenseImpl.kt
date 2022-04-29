package dev.atick.movesense.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import com.movesense.mds.Mds
import com.orhanobut.logger.Logger
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
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
                scanResult?.bleDevice?.bluetoothDevice?.let {
                    Logger.w("DEVICE FOUND: $it")
                    onDeviceFound(
                        BtDevice(
                            name = it.name ?: "Unnamed",
                            address = it.address ?: "Unknown",
                            type = it.bluetoothClass?.majorDeviceClass
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
    }
}