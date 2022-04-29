package dev.atick.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.movesense.utils.BleUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleUtils: BleUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleUtils.initialize(this) {
            Logger.i("BLUETOOTH IS READY")
        }
    }

    override fun onResume() {
        bleUtils.setupBluetooth(this)
        super.onResume()
    }
}