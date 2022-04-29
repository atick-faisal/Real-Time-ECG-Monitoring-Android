package dev.atick.movesense.repository

import com.movesense.mds.Mds
import com.orhanobut.logger.Logger
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MovesenseImpl @Inject constructor(
    private val mds: Mds?,
    private val rxBleClient: RxBleClient?
) : Movesense {

    private var scanDisposable: Disposable? = null

    override fun startScan() {
        Logger.i("SCANNING ... ")
        scanDisposable = rxBleClient?.scanBleDevices(
            ScanSettings.Builder()
                // .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                // .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build()
        )?.subscribe(
            { scanResult ->
                Logger.w("DEVICE FOUND: $scanResult")
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