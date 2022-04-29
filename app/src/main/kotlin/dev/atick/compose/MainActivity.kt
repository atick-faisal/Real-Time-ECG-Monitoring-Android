package dev.atick.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.movesense.repository.Movesense
import dev.atick.movesense.utils.BleUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleUtils: BleUtils
    @Inject
    lateinit var movesense: Movesense

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bleUtils.isAllPermissionsProvided(this))
            movesense.startScan()
        else {
            bleUtils.initialize(this) {
                Logger.i("BLUETOOTH IS READY")
                movesense.startScan()
            }
        }
    }

    override fun onResume() {
        bleUtils.setupBluetooth(this)
        super.onResume()
    }

    override fun onDestroy() {
        movesense.stopScan()
        super.onDestroy()
    }
}